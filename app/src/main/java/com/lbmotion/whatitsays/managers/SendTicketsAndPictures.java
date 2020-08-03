package com.lbmotion.whatitsays.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.lbmotion.whatitsays.R;
import com.lbmotion.whatitsays.UCApp;
import com.lbmotion.whatitsays.communication.Base;
import com.lbmotion.whatitsays.communication.Photo;
import com.lbmotion.whatitsays.communication.SendError;
import com.lbmotion.whatitsays.communication.SendLocation;
import com.lbmotion.whatitsays.communication.Ticket;
import com.lbmotion.whatitsays.communication.VerifyDelivery;
import com.lbmotion.whatitsays.data.TicketInformation;
import com.lbmotion.whatitsays.recording.MediaRecorderRecipe;
import com.lbmotion.whatitsays.recording.VoiceRecording;
import com.lbmotion.whatitsays.service.TicketsAndPicturesService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendTicketsAndPictures implements Runnable {
	private static final String TAG = "SendTicketsAndPictures";
	public AtomicBoolean hasToLoadOnlyTickets = new AtomicBoolean(false);
	private long deltaTimeWithNoPictureLoaded = 0;
	private static ExecutorService threadPool;
	private long lastTrackingSend = 0;
	private HashMap<String, Integer> retryServerError = new HashMap<String, Integer>();
	private HashMap<String, Integer> retryPictureServerError = new HashMap<String, Integer>();
	private HashMap<String, Integer> ticketPorts = new HashMap<String, Integer>();
	public static HashMap<String, String> retryServerErrorNofityUser = new HashMap<String, String>();
	private HashMap<String, Integer> photoPorts = new HashMap<String, Integer>();
	private int firstTime = 2;
	private static Object sync = new Object();
	private TicketsAndPicturesService ticketsAndPicturesService;
	public static SendTicketsAndPictures instance = null;
	public Vector<String> sendTicketFileNames = new Vector<>();

	public static Vector<String> readImages() {
		Vector<String> picturesToSend = null;
		FileInputStream inputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			inputStream = UCApp.gContext.openFileInput("PIC_FILES");
			objectInputStream = new ObjectInputStream(inputStream);
			picturesToSend = new Vector<String>();
			while (true) {
				picturesToSend.add(objectInputStream.readUTF());
				Log.i(TAG, "readImages:" + picturesToSend.get(picturesToSend.size() - 1));

			}
		} catch (Exception e) {
		}
		try {
			objectInputStream.close();
		} catch (Exception e) {
		}
		try {
			inputStream.close();
		} catch (Exception e) {
		}
		if (picturesToSend == null)
			picturesToSend = new Vector<String>();
		return picturesToSend;
	}

	public static void writeImages(Vector<String> picturesToSend) {
		FileOutputStream outputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			File file = UCApp.gContext.getFileStreamPath("PIC_FILES");
			file.delete();
			file.createNewFile();
			outputStream = UCApp.gContext.openFileOutput("PIC_FILES", Context.MODE_PRIVATE);
			objectOutputStream = new ObjectOutputStream(outputStream);
			for (String s : picturesToSend) {
				objectOutputStream.writeUTF(s);
				Log.i(TAG, "writeImages:" + s);
			}
		} catch (Exception e) {
		}
		try {
			objectOutputStream.flush();
		} catch (Exception e) {
		}
		try {
			outputStream.flush();
		} catch (Exception e) {
		}
		try {
			objectOutputStream.close();
		} catch (Exception e) {
		}
		try {
			outputStream.close();
		} catch (Exception e) {
		}
	}

	public static void addImages(Vector<String> pictures) {
		synchronized (sync) {
			Vector<String> newPictures = new Vector<String>();
			Vector<String> picturesToSend = readImages();
			for (String newName : pictures) {
				boolean found = false;
				for (String name : picturesToSend) {
					if (newName.equals(name)) {
						found = true;
						break;
					}
				}
				if (!found) {
					newPictures.add(newName);
				}
			}
			picturesToSend.addAll(newPictures);
			writeImages(picturesToSend);
		}
	}

	private void removeImage(String picture) {
		synchronized (sync) {
			Vector<String> picturesToSend = readImages();
			for (int i = 0; i < picturesToSend.size(); i++) {
				if (picturesToSend.get(i).equals(picture)) {
					picturesToSend.removeElementAt(i);
				}
			}
			writeImages(picturesToSend);
		}
	}

	public static int getNumberOfTicketsWaiting() {
		int count = 0;
		try {
			File dir = TicketInformation.openFile(TicketInformation.getPath("Data"));
			File[] files = dir.listFiles();
			for (int i = 0; files != null && i < files.length; i++) {
				String filename = files[i].getName();
				if (filename.compareTo("Ticket") != 0 && filename.indexOf("Ticket") >= 0)
					count++;
			}
		}
		catch (Exception e) {}
		return count;
	}

	public static int getNumberOfPicturesWaiting() {
		int count = 0;
		try {
			File dir = TicketInformation.openFile(TicketInformation.getPath("Data"));
			File[] files = dir.listFiles();
			for (int i = 0; files != null && i < files.length; i++) {
				if (files[i].getName().indexOf("T") == -1)
					count++;
			}
		}
		catch (Exception e) {}
		return count;
	}

	public SendTicketsAndPictures(TicketsAndPicturesService ticketsAndPicturesService) {
		try {
			instance = this;
			this.ticketsAndPicturesService = ticketsAndPicturesService;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
			retryServerErrorNofityUser = new HashMap<String, String>();
			if (threadPool == null)
				threadPool = Executors.newFixedThreadPool(1);
			if (threadPool != null)
				threadPool.execute(this);
		} catch (Exception re) {
		}
	}

	public static void stopIt() {
		try {
			threadPool.shutdown();
			instance.ticketsAndPicturesService.doStopForeground();
			instance.ticketsAndPicturesService.sendTicketsAndPictures = null;
			instance.ticketsAndPicturesService = null;
			instance = null;
			UCApp.ticketInformation.inProccess = false;
			UCApp.ticketInformation.didCrashInTheMiddle = false;
		} catch (Exception re) {
		}
	}

	private void handelSendError(int onDisk) {
		Vector<String> leftImages = readImages();
		int toSend = leftImages.size();
		if (onDisk < toSend) {
			String listBackupHide = "";
			String listBackup = "";
			String list = "";
			try {
				File dir = TicketInformation.openFile(TicketInformation.getPath("PicData"));
				File[] files = dir.listFiles();
				for (int i = 0; files != null && i < files.length; i++) {
					String filename = files[i].getName();
					for (int j = 0; j < leftImages.size(); j++) {
						if (filename.equals(leftImages.get(j))) {
							listBackup += filename + ",";
							File fileDataNew = TicketInformation.openFile(TicketInformation.getPath("Data") + filename);
							if (fileDataNew.exists()) {
								listBackup += "exists,";
							} else {
								if (!copy(files[i], fileDataNew))
									listBackup += ",fail copy";
							}
							break;
						}
					}
				}
				String directory = TicketInformation.getPathHide("PicData");
				dir = TicketInformation.openFileHide(directory.substring(0, directory.length() - 1));
				files = dir.listFiles();
				for (int i = 0; files != null && i < files.length; i++) {
					String filename = files[i].getName();
					for (int j = 0; j < leftImages.size(); j++) {
						if (filename.equals(leftImages.get(j))) {
							listBackupHide += filename + ",";
							File fileDataNew = TicketInformation.openFile(TicketInformation.getPath("Data") + filename);
							if (fileDataNew.exists()) {
								listBackupHide += "exists,";
							} else {
								if (!copy(files[i], fileDataNew))
									listBackupHide += ",fail copy";
							}
							break;
						}
					}
				}
				list = leftImages.get(0);
				if (toSend > 0) {
					for (int i = 1; i < leftImages.size(); i++)
						list += "," + leftImages.get(i);
				}
			} catch (Exception e) {
			}
			SendError.send(UCApp.loginData.authority, UCApp.loginData.username, "on disk:" + onDisk + " to send:" + toSend + " " + list + "::Backup::" + listBackup + "::Hide::" + listBackupHide);
			writeImages(new Vector<String>());
		} else if (onDisk == 0 && toSend == 0) {
			clearFilesInBackup();
		}
	}

	public boolean copy(File src, File dst) throws IOException {
		boolean returnError = false;
		InputStream in = null;
		try {
			in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);
			try {
				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
			} catch (Exception e) {
				returnError = true;
			} finally {
				try {
					out.flush();
				} catch (Exception e) {
				}
				try {
					out.close();
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			returnError = true;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return returnError;
	}

	private void clearFilesInBackup() {
		try {
			File dir = TicketInformation.openFile(TicketInformation.getPath("PicData"));
			File[] files = dir.listFiles();
			for (int i = 0; files != null && i < files.length; i++) {
				File fileDataNew = TicketInformation.openFile(TicketInformation.getPath("Data") + files[i].getName());
				if (!fileDataNew.exists())
					files[i].renameTo(fileDataNew);
			}
			String directory = TicketInformation.getPathHide("PicData");
			dir = TicketInformation.openFileHide(directory.substring(0, directory.length() - 1));
			files = dir.listFiles();
			for (int i = 0; files != null && i < files.length; i++) {
				File fileDataNew = TicketInformation.openFile(TicketInformation.getPath("Data") + files[i].getName());
				if (!fileDataNew.exists())
					copy(files[i],fileDataNew);
			}
		}
		catch (Exception e) {}
	}

	@Override
	public void run() {
		long wait = 0;
		while(!threadPool.isShutdown()) {
            try {
            	if(!threadPool.isShutdown()) {
					try {ticketsAndPicturesService.doStartForeground();} catch (Exception e) {}
				}
				if(hasToLoadOnlyTickets!= null)
					Log.i(TAG,"run()"+hasToLoadOnlyTickets.get());
				doSendTickets();
                try {
                    Thread.sleep(wait);}catch (InterruptedException ie) {}
                boolean sendPicture = false;
				for(int i = 0;i < sendTicketFileNames.size() && !sendPicture;i++) {
					String filename = sendTicketFileNames.get(i);
					int count = 0;
					for (int j = 0; j < sendTicketFileNames.size(); j++) {
						if(filename.equals(sendTicketFileNames.get(j))) {
							if(++count > 4) {
								sendPicture = true;
								break;
							}
						}
					}
				}
				if(!TicketInformation.hasToLoadTickets() || sendPicture) {
					sendTicketFileNames.removeAllElements();
					wait = doSendPicture();
					try {
						try { Thread.sleep(wait); } catch (InterruptedException ie) {}
					}
					catch (Exception e) {}
					catch (Error e) {}
					int onDisk = getNumberOfPicturesWaiting();
					if(onDisk == 0)
						doSendPrintedTickets();
					handelSendError(onDisk);
					if (hasToLoadOnlyTickets != null && hasToLoadOnlyTickets.get()) {
						if (deltaTimeWithNoPictureLoaded == 0)
							deltaTimeWithNoPictureLoaded = (new Date().getTime());
						else if ((new Date().getTime()) - deltaTimeWithNoPictureLoaded > 60 * 60 * 1000L)
							hasToLoadOnlyTickets.set(false);
					} else {
						deltaTimeWithNoPictureLoaded = 0;
					}
				}
/*
				if(UCApp.loginData.tracking != 0) {
					if((new Date()).getTime() > lastTrackingSend) {
						lastTrackingSend = (new Date()).getTime()+ Math.max(UCApp.loginData.tracking,30)*1000;
						Location location = LocationDetection.getLocation();
						if (location != null) {
							sendLocation(location.getLatitude(),location.getLongitude());
							LocationDetection.stop();
						}
						LocationDetection.start();
					}
				}
*/
			}
            catch (Exception e) {}
		}
	}
	
	private void doSendTickets() {
		try {
			File dir = TicketInformation.openFile(TicketInformation.getPath("Data"));
			File[] files = dir.listFiles();
			for(int i = 0;files != null && i < files.length;i++) {
				String filename = files[i].getName();
				if(filename.compareTo("Ticket") != 0 && filename.indexOf("Ticket") >= 0) {
					try {
						if(UCApp.ticketInformation.filename.length() > 0 && UCApp.ticketInformation.filename.equals(filename) && firstTime <= 0) {
							if(SendTicketsInformation.search(UCApp.ticketInformation.filename) < 30)
								continue;
						}
						String fn = filename;
						TicketInformation ti = TicketInformation.getTicket(filename);
						if(ti.timestampFile == -1 || ti.timestampFile+5000 > (new Date()).getTime()) {
							if(SendTicketsInformation.search(UCApp.ticketInformation.filename) < 30 && firstTime <= 0)
								continue;
						}
						if(ti.filename.length() == 0) {
							ti.filename = fn+""+ti.UserCounter+""+ti.LK;
							fn = ti.filename;
						}
						if(ti != null) {
							sendTicketFileNames.add(filename);
							if(retryServerError.get(fn) == null || retryServerError.get(fn).intValue() < 5) {
								toastMessage("doSendTickets():"+filename+"       "+ti.dochCode);
								boolean sendTicket = true;
								if(ticketPorts.get(fn) != null) {
									short port = ticketPorts.get(fn).shortValue();
									VerifyDelivery verifyDelivery = VerifyDelivery.doCheck(fn, UCApp.loginData.Lk+"",port);
									if(verifyDelivery.result) {
										if(verifyDelivery.arrived) {
											TicketInformation.deleteFile(filename);
											ticketPorts.remove(fn);
											sendTicket = false;
										}
									}
								}
								if(sendTicket) {
									Ticket ticket = Ticket.doSend(ti);
									if(ti.doch.length() > 0 || (ti.dochCode != -1 && ti.dochCode != 0)) {
										ticketPorts.put(fn, Integer.valueOf(ticket.portBackgroundUsed));
									}
									if(ticket.result) {
										if(retryServerError.get(fn) != null)
											retryServerError.remove(fn);
										if(ti.doch.length() == 0 && (ti.dochCode == -1 || (ti.dochCode == 0) && ti.packachDecisionCode < 3)) {
											TicketInformation.deleteFile(filename);
										}
									}
									else if(ticket.errorFromServer) {
										Integer tries = Integer.valueOf(0);
										if(retryServerError.get(fn) != null)
											tries = retryServerError.get(fn);
										tries++;
										retryServerError.put(fn, tries);
									}
								}
							}
							else {
								if(retryServerErrorNofityUser.get(fn) == null) {
									String message;
									if(ti.p_printedDochNumber.length() > 0)
										message = ti.p_printedDochNumber;
									else if(ti.openEvent)
										message = Base.mContext.getString(R.string.ticket_error_server_type1);
									else
										message = Base.mContext.getString(R.string.ticket_error_server_type2);
									retryServerErrorNofityUser.put(fn, message);
									(new AsyncTask<String, Integer, Boolean>() {
										String message = "";
										@Override
										protected Boolean doInBackground(String... params) {
											message = params[0];
											try {
                                                Thread.sleep(125);}catch (InterruptedException ie) {}
											return Boolean.valueOf(true);
										}
										@Override
										protected void onPostExecute(Boolean result) {
											try {
												Toast.makeText(Base.mContext, Base.mContext.getString(R.string.ticket_error_server) + " " + message, Toast.LENGTH_LONG).show();
											}
											catch (Exception e) {}
											catch (Error e) {}
										}
									}).execute(message);
								}
							}
						}
					}
					catch(Exception e) {
						toastMessage("Error:"+e.getMessage());
					}
					try {
                        Thread.sleep(500);}catch (InterruptedException ie) {}
				}
			}
			if(firstTime > 0)
				firstTime--;
		}
		catch(Exception e) {}
	}

	private int doSendPicture() {
		int returnValue = 6000;
		try {
			if(!hasToLoadOnlyTickets.get()) {
				toastMessage("doSendPicture(): start");
				File dir = TicketInformation.openFile(TicketInformation.getPath("Data"));
				File[] files = dir.listFiles();
				for(int i = 0;files != null && i < files.length;i++) {
					String filename = files[i].getName();
					if(filename.indexOf("T") == -1 && files[i].lastModified()+10000 < (new Date()).getTime()) {
						if(retryPictureServerError.get(filename) == null || retryPictureServerError.get(filename).intValue() < 5) {
							if(VoiceRecording.recordingNow.get() || MediaRecorderRecipe.recordingNow.get())
								return 1000;
							boolean skip = false;
							for(int n = 0;n < UCApp.ticketInformation.pictures.size() && !skip;n++) {
								if(UCApp.ticketInformation.pictures.get(n).equals(filename)) {
									skip = true;
									break;
								}
							}
							if(UCApp.extraPicture != null && UCApp.extraPicture.equals(filename)) {
								skip = true;
							}
							for(int n = 0;n < UCApp.ticketInformation.sounds.size() && !skip;n++) {
								if(UCApp.ticketInformation.sounds.get(n).indexOf(filename) != -1) {
									skip = true;
									break;
								}
							}
							if(skip) {
								toastMessage("doSendPicture(): skip "+filename);
								continue;
							}
							boolean sendFromPicData = false;
							boolean sendFromPicDataHide = false;
							if(!files[i].exists() || TicketInformation.getFileSize(filename) == 0) {
								try {
                                    Thread.sleep(250);} catch (Exception e) {}
								System.gc();
								if(!files[i].exists() || TicketInformation.getFileSize(filename) == 0) {
									sendFromPicData = true;
									if(!TicketInformation.openFile(TicketInformation.getPath("PicData")+filename).exists() || TicketInformation.getFilePicDataSize(filename) == 0) {
										sendFromPicDataHide = true;
										if (!TicketInformation.openFileHide(TicketInformation.getPathHide("PicData") + filename).exists() || TicketInformation.getFilePicDataSizeHide(filename) == 0) {
											SendError.send(UCApp.loginData.authority, UCApp.loginData.username, "Zero picture file length:" + filename);
											TicketInformation.deleteFile(filename);
											TicketInformation.deletePicDataFile(filename);
											TicketInformation.deletePicDataFileHide(filename);
											removeImage(filename);
											continue;
										}
									}
								}
							}
							boolean sendPhoto = true;
							toastMessage("doSendPicture(): check "+filename);
							if(photoPorts.get(filename) != null) {
								toastMessage("doSendPicture(): verify "+filename);
								short port = photoPorts.get(filename).shortValue();
								VerifyDelivery verifyDelivery = VerifyDelivery.doCheck(filename,UCApp.loginData.Lk+"",port);
								if(verifyDelivery.result) {
									toastMessage("doSendPicture(): verify OK"+filename);
									if(verifyDelivery.arrived) {
										toastMessage("doSendPicture(): verify arrived"+filename);
										boolean okToSend = true;
										for(int n = 0;n < UCApp.ticketInformation.pictures.size();n++) {
											if(UCApp.ticketInformation.pictures.get(n).equals(filename)) {
												okToSend = false;
												break;
											}
										}
										if(UCApp.extraPicture != null && UCApp.extraPicture.equals(filename)) {
											okToSend = false;
										}
										for(int n = 0;n < UCApp.ticketInformation.sounds.size() && okToSend;n++) {
											if(UCApp.ticketInformation.sounds.get(n).indexOf(filename) != -1) {
												okToSend = false;
												break;
											}
										}
										if(okToSend) {
											toastMessage("doSendPicture(): verify arrived and save"+filename);
											TicketInformation.deleteFile(filename);
											TicketInformation.deletePicDataFile(filename);
											TicketInformation.deletePicDataFileHide(filename);
											photoPorts.remove(filename);
											removeImage(filename);
											returnValue = 500;
											break;
										}
										sendPhoto = false;
									}
								}
							}
							if(sendPhoto) {
								toastMessage("doSendPicture(): send picture "+filename);
								Photo photo = Photo.doSave(filename,UCApp.loginData.Lk+"",sendFromPicData,sendFromPicDataHide);
								if(photo.result) {
									photoPorts.put(filename, Integer.valueOf(photo.portBackgroundUsed));
									toastMessage("doSendPicture(): send picture ok "+filename);
								}
								else if(photo.errorFromServer) {
									toastMessage("doSendPicture(): send picture error "+filename);
									Integer tries = Integer.valueOf(0);
									if(retryPictureServerError.get(filename) != null)
										tries = retryPictureServerError.get(filename);
									tries++;
									retryPictureServerError.put(filename, tries);
								}
								break;
							}
						}
					}
				}
			}
		}
		catch(Exception e) {}
		return returnValue;
	}
	
	private void sendLocation(double latitude,double  longitude) {
		if(SendLocation.doSend(UCApp.loginData.authority,latitude,longitude).result) {
			lastTrackingSend = (new Date()).getTime()+ Math.max(UCApp.loginData.tracking,30)*1000;
		}
	}

	private static class SendTicketsInformation {
		public static Vector<SendTicketsInformation> sendTicketsInformations = new Vector<SendTicketsInformation>();
		public String filename = "";
		public int 									 counter = 0;

		public static int search(String name) {
			for(int i = 0;i < sendTicketsInformations.size();i++) {
				SendTicketsInformation sendTicketsInformation = sendTicketsInformations.get(i);
				if(sendTicketsInformation.filename.equals(name)) {
					sendTicketsInformation.counter++;
					return sendTicketsInformation.counter;
				}
			}
			SendTicketsInformation sendTicketsInformation = new SendTicketsInformation();
			sendTicketsInformation.filename = name;
			sendTicketsInformations.add(sendTicketsInformation);
			return sendTicketsInformation.counter;
		}
	}

	public static class FileDate {
		public static final Comparator SortIt = new Comparator() {
			public int compare(Object o1, Object o2) {
				return (int)(((File)o1).lastModified()-((File)o2).lastModified());
			}
		};
	}

	private void doSendPrintedTickets() {
		if(hasToLoadOnlyTickets != null && !hasToLoadOnlyTickets.get()) {
			try {
				File dir = TicketInformation.openFile(TicketInformation.getPath("Prints"));
				File[] dirFiles = dir.listFiles();
				if (dirFiles == null)
					return;
				SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UCApp.gContext);
				long lastSendTime = sharedPreferences.getLong("lastSendTime", 0);
				Vector<File> files = new Vector<File>();
				for (int i = 0; i < dirFiles.length; i++)
					if (dirFiles[i].lastModified() > lastSendTime)
						files.add(dirFiles[i]);
				Collections.sort(files, FileDate.SortIt);
				for (int i = 0; i < files.size(); i++) {
					String fn = files.get(i).getName();
					TicketInformation ti = TicketInformation.getPrintedTicket(fn);
					Ticket ticket = Ticket.doSend(ti, true);
					if (ticket.result) {
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putLong("lastSendTime", files.get(i).lastModified());
						editor.commit();
						TicketInformation.deletePrintedTicketHide(fn);
					}
					break;
				}
				if(files.size() == 0) {
					String directory = TicketInformation.getPathHide("Prints");
					dir = TicketInformation.openFileHide(directory.substring(0,directory.length()-1));
					dirFiles = dir.listFiles();
					if(dirFiles != null && dirFiles.length > 0) {
						TicketInformation ti = TicketInformation.getPrintedTicketHide(dirFiles[0].getName());
						Ticket ticket = Ticket.doSend(ti, true);
						if (ticket.result)
							TicketInformation.deletePrintedTicketHide(dirFiles[0].getName());
					}
				}
			}
			catch (Exception e) {}
		}
	}

	private void toastMessage(final String message) {
//		(new AsyncTask<String, Integer, Boolean>() {
//			@Override
//			protected Boolean doInBackground(String... params) {
//				try {Thread.sleep(125);} catch (Exception e) {}
//				return Boolean.valueOf(true);
//			}
//			@Override
//			protected void onPostExecute(Boolean result) {
//				Toast.makeText(UCApp.gContext,(new Date()).toString()+" "+message , Toast.LENGTH_SHORT).show();
//			}
//		}).execute();
	}
}
