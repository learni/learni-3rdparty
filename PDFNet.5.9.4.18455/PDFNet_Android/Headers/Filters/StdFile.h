//---------------------------------------------------------------------------------------
// Copyright (c) 2001-2013 by PDFTron Systems Inc. All Rights Reserved.
// Consult legal.txt regarding legal and license information.
//---------------------------------------------------------------------------------------

#ifndef	  H_CPPFilterStdFile
#define	  H_CPPFilterStdFile


#include <Common/UString.h>
#include <Filters/Filter.h>
#include <Common/Common.h>
#include <C/Filters/TRN_Filter.h>

namespace pdftron { 
	namespace Filters {

/** 
 * StdFile is a utility class to read from, write to, open, and close files on a 
 * file system. Because StdFile file is derived from pdftron.Filters.Filter you can 
 * directly chain StdFile objects to other 'pdftron.Filters'.
 *
 * StdFile objects support random access to files using the Seek method. Seek 
 * allows the read/write position to be moved to any position within the file. This 
 * is done with byte offset reference point parameters. The byte offset is relative 
 * to the seek reference point, which can be the beginning, the current position, 
 * or the end of the underlying file, as represented by the three properties of the 
 * Fileter.ReferencePos class.
 * 
 * Disk files always support random access. At the time of construction, the CanSeek()
 * property value is set to true or false depending on the underlying file type.
 * 
 * @note .NET or Java applications should explicitly Close() files when they are not needed.
 * If the files are not closed or disposed this may lead to the resource exhaustion.
 */ 
class StdFile : public Filter
{
public:

	enum OpenMode
	{
		e_read_mode,   ///< Opens file for reading. An exception is thrown if the file doesn't exist.
		e_write_mode,  ///< Opens an empty file for writing. If the given file exists, its contents are destroyed. 
		e_append_mode, ///< Opens for reading and appending. Creates the file first if it doesn't exist. 
	};

	/**
	 * Create a new instance of StdFile class with the specified path and creation mode
	 */
	StdFile (const UString& filename, OpenMode open_mode, size_t buf_sz = 1024);
	StdFile (const char* filename, OpenMode open_mode, size_t buf_sz = 1024);

#ifndef SWIG
	/**
	 * Create a new instance of StdFile class using the given instance of standard
	 * file stream. 
	 *
	 * @note StdFile does not take the ownership of the file stream and the calling 
	 * function is responsible for closing the file.     
	 */
	StdFile (FILE* stm, OpenMode open_mode, size_t buf_sz = 1024);
#endif
	
	/**
	 * Check is the two StdFile-s refer to the same file on disc. 
	 * @return true if the two files share the same filename, 
	 * false otherwise. 
	 */
	bool operator == (const StdFile& f) const;

	/**
	 * Check is the two file paths are equivalent (i.e. they refer to the 
	 * same file on disc. 
	 * @return true if the two files share the same filename, false otherwise. 
	 */
	static bool Equivalent( const UString& ph1, const UString& ph2);

	/**
	 * @return the size of the current file.
	 */
	size_t FileSize();


};

#include <Impl/StdFile.inl>
	
	};	// namespace Filters
};	// namespace pdftron

#endif


