package com.util;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

/**
 * 日志类<br />
 * 仅提供控制台和文件的输出<br />
 * 初始化时需设定LOG_DIRECTORY
 * 
 * @author adam
 * 
 */
public class AppLog {

	// 日志记录到什么地方
	private final static int LogToNothing = 0;
	private final static int LogToConsole = 1;
	private final static int LogToFile = 2;
	// private final static int LogToDB = 4;
	// private final static int LogToMail = 8;

	// 日志级别
	private final static int LogLevelDebug = 0;
	private final static int LogLevelInfo = 1;
	private final static int LogLevelError = 2;

	// 日志操作
	public static int LOG_HANDLE_DEBUG = LogToConsole;
	public static int LOG_HANDLE_INFO = LogToConsole + LogToFile;
	public static int LOG_HANDLE_ERROR = LogToConsole + LogToFile;

	/**
	 * 日志目录
	 */
	public static String LOG_DIRECTORY = null;

	/**
	 * 是否自动取得调用者的信息
	 */
	public static boolean LOG_AUTO_GET_CALLER = true;

	public static void debug(Object message) {
		int stackTraceIndex = 0;
		String className = null;
		String methodName = null;
		int lineNumber = 0;

		if (LOG_AUTO_GET_CALLER) {
			try {
				stackTraceIndex = getStackTraceIndex();
				className = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getClassName();
				methodName = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getMethodName();
				lineNumber = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getLineNumber();

			} catch (Exception e) {
				System.out.println("Log.debug");
				e.printStackTrace();
			}
		}
//		log(className, methodName, lineNumber, message, LogLevelDebug,
//				LOG_HANDLE_DEBUG);
	}

	public static void info(Object message) {
		int stackTraceIndex = 0;
		String className = null;
		String methodName = null;
		int lineNumber = 0;

		if (LOG_AUTO_GET_CALLER) {
			try {
				stackTraceIndex = getStackTraceIndex();
				className = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getClassName();
				methodName = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getMethodName();
				lineNumber = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getLineNumber();

			} catch (Exception e) {
				System.out.println("Log.info");
				e.printStackTrace();
			}
		}

		log(className, methodName, lineNumber, message, LogLevelInfo,
				LOG_HANDLE_INFO);
	}

	public static void error(Object message) {
		int stackTraceIndex = 0;
		String className = null;
		String methodName = null;
		int lineNumber = 0;

		if (LOG_AUTO_GET_CALLER) {
			try {
				stackTraceIndex = getStackTraceIndex();
				className = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getClassName();
				methodName = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getMethodName();
				lineNumber = Thread.currentThread().getStackTrace()[stackTraceIndex]
						.getLineNumber();

			} catch (Exception e) {
				System.out.println("Log.error");
				e.printStackTrace();
			}
		}
		log(className, methodName, lineNumber, message, LogLevelError,
				LOG_HANDLE_ERROR);
	}

	/**
	 * 输出到控制台
	 * 
	 * @param message
	 */
	private static void log(String className, String methodName,
                            int lineNumber, Object message, int logLevel, int logHandle) {

		if (logHandle == LogToNothing) {
			return;
		}

		if ((logHandle & LogToConsole) != 0) {
			log2console(className, methodName, lineNumber, message, logLevel);
		}

		if ((logHandle & LogToFile) != 0) {
			log2file(className, methodName, lineNumber, message, logLevel);
		}

	}

	/**
	 * 输出到控制台
	 * 
	 * @param message
	 */
	@SuppressLint("SimpleDateFormat")
	private static void log2console(String className, String methodName,
                                    int lineNumber, Object message, int logLevel) {

		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		if (className != null)
			System.out.print("className:" + className + ";");
		if (methodName != null)
			System.out.print("methodName:" + methodName + ";");
		if (lineNumber != 0)
			System.out.println("lineNumber:" + lineNumber + ";");

		System.out.println("--------------------");

		if (message == null) {
			System.out.println("null");
		} else if (message instanceof Exception) {
			((Exception) message).printStackTrace();

			// StringWriter errors = new StringWriter();
			// ((Exception) message).printStackTrace(new PrintWriter(errors));
			// System.out.println(errors.toString());

		} else {
			System.out.println(message);
		}
		System.out.println("--------------------time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(System.currentTimeMillis()));
	}

	/**
	 * 
	 * @param
	 * @param message
	 * @param logLevel
	 */
	@SuppressLint("SimpleDateFormat")
	private static void log2file(String className, String methodName,
                                 int lineNumber, Object message, int logLevel) {

		StringBuilder prefix = new StringBuilder();

		// 目录
		prefix.append(LOG_DIRECTORY);
		prefix.append("/");

		// 日志级别
		switch (logLevel) {
		case LogLevelDebug:
			prefix.append("debug_");
			break;
		case LogLevelInfo:
			prefix.append("info_");
			break;
		case LogLevelError:
			prefix.append("error_");
			break;
		default:
			break;
		}

		if (className != null) {
			prefix.append(className);
			prefix.append('_');
		}

		prefix.append(new SimpleDateFormat("yyyy-MM-dd").format(System
				.currentTimeMillis()));
		prefix.append(".log");

		//
		File file = null;
		FileWriter fw = null;

		try {
			file = new File(prefix.toString());
			fw = new FileWriter(file, true);

			if (className != null)
				fw.append("className:" + className + "\n");
			if (methodName != null)
				fw.append("methodName:" + methodName + "\n");
			if (lineNumber != 0)
				fw.append("lineNumber:" + lineNumber + "\n");

			if (message == null) {
				fw.append("null");
			} else if (message instanceof Exception) {
				StringWriter sw = new StringWriter();
				((Exception) message).printStackTrace(new PrintWriter(sw));
				fw.append("message:" + sw.toString());
			} else {
				fw.append(message.toString());
			}
			fw.append("\n");
			fw.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
					.format(System.currentTimeMillis()));
			fw.append("\n");

			fw.flush();
		} catch (IOException e) {
			// ignore
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static int getStackTraceIndex() {
		int ret = 2;
		String logClassName = "com.spzjs.b7core.AppLog";
		String className = null;

		for (int i = 2; i < Thread.currentThread().getStackTrace().length; i++) {
			className = Thread.currentThread().getStackTrace()[i].getClassName();
			if(className == null) continue;

			if(className.equals(logClassName)) continue;

			if(className.length() > logClassName.length()){
				if(className.substring(0, logClassName.length()).equals(logClassName)){
					continue;
				}
			}

			ret = i;
			break;
		}

		return ret;
	}

}
