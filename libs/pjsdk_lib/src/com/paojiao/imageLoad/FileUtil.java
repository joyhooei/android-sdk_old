package com.paojiao.imageLoad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.graphics.Bitmap;

public class FileUtil {

	/**
	 * 得到String的HashCode值
	 * 
	 * @param value
	 * @return
	 */
	public static String getStringHashCode(String value) {
		return String.valueOf(value.hashCode());
	}

	/**
	 * 保存bitmap 如果此文件已经存在就直接返回
	 * 
	 * @param fileDir
	 * @param fileName
	 * @param bitmap
	 * @return
	 */
	public static File saveBitmapIfNotIn(File fileDir, String fileName,
			Bitmap bitmap) {
		File file = new File(fileDir, fileName);
		if (file.exists() && !file.isDirectory()) {
			return file;
		}
		FileOutputStream outputStream = null;
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);// 把数据写入文件
			outputStream.flush();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭文件流
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 保存bitmap 如果原来文件已经存在则会删除后再保存
	 * 
	 * @param fileDir
	 * @param fileName
	 * @param bitmap
	 * @return
	 */

	public static File saveBitmapEvenIn(File fileDir, String fileName,
			Bitmap bitmap) {
		File file = new File(fileDir, fileName);
		if (file.exists() && !file.isDirectory()) {
			file.delete();
		}
		FileOutputStream outputStream = null;
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);// 把数据写入文件
			outputStream.flush();
			return file;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭文件流
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 保存对象到文件，对象需序列化
	 * 
	 * @param fileDir
	 *            文件目录
	 * @param fileName
	 *            文件名
	 * @param fileData
	 *            保存对象
	 * @return
	 */
	public static Boolean saveObject(File fileDir, String fileName,
			Object fileData) {
		File file = creatFile(fileDir, fileName);
		if (file == null) {
			return false;
		}
		ObjectOutputStream object_outStream = null;
		try {
			object_outStream = new ObjectOutputStream(
					new FileOutputStream(file));
			object_outStream.writeObject(fileData);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				object_outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;

	}

	/**
	 * 读取对象
	 * 
	 * @param fileDir
	 *            文件目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static Object readObject(File fileDir, String fileName) {
		File file = getFile(fileDir, fileName);
		if (file == null) {
			return null;
		}
		ObjectInputStream object_inStream = null;
		try {
			object_inStream = new ObjectInputStream(new FileInputStream(file));
			return object_inStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				object_inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileDir
	 *            文件目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static boolean isFileExist(File fileDir, String fileName) {
		// 判断SD卡上的文件是否存在
		File file = new File(fileDir, fileName);
		return file.exists();
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param absolutePath
	 *            绝对路径
	 * @return
	 */
	public static File isFileExist(String absolutePath) {
		// 判断SD卡上的文件是否存在
		File file = new File(absolutePath);
		if (file.exists())
			return file;
		return null;
	}

	/**
	 * 创建文件目录
	 * 
	 * @param dir
	 * @param childDir
	 * @return
	 */
	public static File createFileDir(File dir, String childDir) {
		File file = new File(dir, childDir);
		if (!file.exists() || !file.isDirectory())
			file.mkdir();
		return file;
	}

	/**
	 * 创建文件
	 * 
	 * @param file
	 *            文件目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static File creatFile(File fileDir, String fileName) {

		File file = new File(fileDir, fileName);
		if (!file.exists() || file.isDirectory()) {
			try {
				file.createNewFile();
				return file;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 获取文件
	 * 
	 * @param fileDir
	 *            文件目录
	 * @param fileName
	 *            文件名
	 * @return
	 */
	public static File getFile(File fileDir, String fileName) {
		File file = new File(fileDir, fileName);

		if (!file.exists()) {
			return null;
		}
		if (file.isDirectory()) {
			return null;
		}
		return file;

	}

	/**
	 * 删除文件夹里面的所有文件以及文件夹
	 * 
	 * @param path
	 */
	public static void delAllFileWithFloder(File cacheDir) {
		if (!cacheDir.exists()) {
			return;
		}

		File[] files = cacheDir.listFiles();
		for (int index = 0; index < files.length; index++) {
			File temp = files[index];
			if (temp.isDirectory()) {
				delAllFileWithFloder(temp);
				temp.delete();
			} else {
				temp.delete();
			}
		}
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 */
	public static void delAllFile(File cacheDir) {
		if (!cacheDir.exists()) {
			return;
		}

		File[] files = cacheDir.listFiles();
		for (int index = 0; index < files.length; index++) {
			File temp = files[index];
			if (temp.isDirectory()) {
				delAllFile(temp);
			} else {
				temp.delete();
			}
		}
	}

}
