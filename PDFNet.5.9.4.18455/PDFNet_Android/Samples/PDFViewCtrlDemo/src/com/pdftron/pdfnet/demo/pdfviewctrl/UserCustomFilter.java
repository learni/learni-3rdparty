//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

package com.pdftron.pdfnet.demo.pdfviewctrl;
import java.io.IOException;
import java.io.RandomAccessFile;

import pdftron.Common.PDFNetException;
import pdftron.Filters.CustomFilter;


/**
 * <p>
 * This class shows how to utilize pdftron.Filters.CustomFilter to customize 
 * the reading and writing procedure.
 * </p>
 */
public class UserCustomFilter extends CustomFilter {
	long mLength = 0;
	
	public UserCustomFilter(int mode, RandomAccessFile raf)
			throws PDFNetException {
		super(mode, raf);
		try {
			mLength = raf.length();
		} catch (IOException e) {
		}
	}

	/**
	 * The following implements the five abstract functions specified in CustomFilter to
	 * customize the reading and writing procedure.
	 * 
	 */
	
	@Override
	public long onRead(byte[] buf, Object user_object) {
		long result = 0;
		RandomAccessFile raf = (RandomAccessFile)user_object;
		try {
			int n = raf.read(buf, 0, buf.length);
			result = n >= 0 ? n : 0; //if failed, return 0.
		} catch (IOException e) {
		}
		return result;
	}

	
	@Override
	public long onSeek(long offset, int origin, Object user_object) {
		int result = 0; //indication of success
		try {
			RandomAccessFile raf = (RandomAccessFile)user_object;
			if ( origin == CustomFilter.SEEK_SET ) {
				raf.seek(offset >= 0 ? offset : 0);
			}
			else if ( origin == CustomFilter.SEEK_CUR ) {
				raf.seek(offset+raf.getFilePointer());
			}
			else if ( origin == CustomFilter.SEEK_END ) {
				raf.seek(mLength+offset);
			}
		} catch (IOException e) {
			result = -1; //indication of failure
		}
		return result;
	}

	
	@Override
	public long onTell(Object user_object) {
		long result = -1; //indication of failure
		RandomAccessFile raf = (RandomAccessFile)user_object;
		try {
			result = raf.getFilePointer();
		} catch (IOException e) {
		}
		return result;
	}

	
	@Override
	public long onFlush(Object user_object) {
		return 0; //always success; no flush needed in this case
	}

	
	@Override
	public long onWrite(byte[] buf, Object user_object) {
		RandomAccessFile raf = (RandomAccessFile)user_object;
		long result = 0;
		try {
			int size = buf.length;
			raf.write(buf, 0, size);
			result = size;
		} catch (IOException e) {
		}
		return result;
	}	
}