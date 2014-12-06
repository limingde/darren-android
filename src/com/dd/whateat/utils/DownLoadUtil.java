/**
 * 提供常用的下载方法，包括文本文件，音频文件的下载，同时满足直接读取文本文件将其显示的显示的方法
 * 建议在使用此工具类，在声明构造方法时传入Activity的上下文（当不是在Activity内调用此方法时，如在某service中下载，最好将上下文传入，
 * 在使用工具类时，传递给构造函数，这样可以获得更明确的前端提示信息，当然，后台同样提供错误信息），方便调试
 *
 */
package com.dd.whateat.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class DownLoadUtil {
	String ERROTAG = "DownLoadUtilError";
	Context c;

	public DownLoadUtil(Context c) {
		if (c != null) {
			this.c = c;
		} else {
			Log.w(ERROTAG, "上下文为空");
		}
	}

	public String getTextFileString(String url) {
		InputStream input = HttpUtils.getStreamFromURL(url);
		StringBuffer sb = new StringBuffer("");
		BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
		String line = "";
		try {
			while ((line = bfr.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			toastError("流文件读写错误");
			e.printStackTrace();
		} finally {
			try {
				bfr.close();
			} catch (IOException e) {
				toastError("流文件未能正常关闭");
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public void downFiletoDecive(String url, String filename) {
		if ((url != null && !"".equals(url))
				&& (filename != null && !"".equals(filename))) {
			InputStream input = HttpUtils.getStreamFromURL(url);
			FileOutputStream outStream = null;
			try {
				outStream = c.openFileOutput(filename,
						Context.MODE_WORLD_READABLE
								| Context.MODE_WORLD_WRITEABLE);
				int temp = 0;
				byte[] data = new byte[1024];
				while ((temp = input.read(data)) != -1) {
					outStream.write(data, 0, temp);
				}
			} catch (FileNotFoundException e) {
				toastError("请传入正确的上下文");
				e.printStackTrace();
			} catch (IOException e) {
				toasteMessage("读写错误");
				e.printStackTrace();
			} finally {
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException e) {
					toastError("流文件未能正常关闭");
					e.printStackTrace();
				}

			}
		}
		toasteMessage("下载成功");
	}

	public void downFiletoSDCard(String url, String path, String filename) {

		if ((url != null && !"".equals(url)) && (path != null)
				&& (filename != null && !"".equals(filename))) {

			InputStream input = HttpUtils.getStreamFromURL(url);
			downloader(input, path, filename);

		} else {
			/*
			 * 对不合发的参数做处理
			 */
			if (url == null || "".equals(url)) {
				toastError("url不能为空或为“”");
			}
			if (path == null) {
				toastError("path不能为空");
			}
			if (filename == null || "".equals(filename)) {
				toastError("filename不能为空");
			}
		}

	}

	private void downloader(InputStream input, String path, String name) {
		String realpath = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			realpath = Environment.getExternalStorageDirectory() + path;
		} else {
			toastError("SDCard异常，请检查是否存在SDCard并确认其状态和程序的访问权限");
		}

		if (!dirsExits(realpath)) {
			creatDir(realpath);
		}
		if (!fileExits(realpath + "/" + name)) {
			makeFile(input, realpath, name);
		} else {
			toastError("已存在的命名的文件，请删掉原有文件或重命名");
		}
	}

	private void toastError(String message) {
		if (c != null) {
			Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
		}
		try {
			throw new Exception(message);
		} catch (Exception e) {
			Log.w(ERROTAG, "未能捕获所有异常");
			e.printStackTrace();
		}
	}

	private void toasteMessage(String message) {
		if (c != null) {
			Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
		}
		Log.i(ERROTAG, message);
	}
	
	/**
	 * 判断文件存放文件的目录是否存在，同时对外提供接口
	 * @param path 存放文件目录
	 * @return
	 */
	public boolean dirsExits(String path){
		File file = new File(path);
		Log.i(ERROTAG, "文件"+path+"存在情况为："+file.exists());
		return file.exists();
	}
	
	
	/**
	 * 判断文件是否存在，同时对外提供接口
	 * @param path 文件路径及名称
	 * @return
	 */
	public boolean fileExits(String path){
		return dirsExits(path);
	}
	
	/**
	 * 创建指定的目录
	 * @param path 目录的路径
	 */
	private void creatDir(String path){
		File file = new File(path);
		file.mkdirs();
	}
	
	/**
	 * 创建文件
	 * @param input 输入流
	 * @param realpath 真实路径
	 * @param name 文件存放的名字
	 */
	private void makeFile(InputStream input,String realpath,String name){
		File file = new File(realpath+"/"+name);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			int temp = 0;
			byte[] data = new byte[1024];
			while((temp = input.read(data))!=-1){
				out.write(data, 0, temp);
			}
		} catch (FileNotFoundException e) {
			toastError("创建文件失败");
			e.printStackTrace();
		} catch (IOException e) {
			toastError("读写错误");
			e.printStackTrace();
		}finally{
			try {
				out.flush();
			} catch (IOException e) {
				toastError("流文件未能正常关闭");
				e.printStackTrace();
			}
			
			FileUtil.closeIO(out);
		}
		toasteMessage("下载成功");
	}
}
