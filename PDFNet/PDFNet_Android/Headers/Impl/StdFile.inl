
inline StdFile::StdFile (const UString& filename, OpenMode open_mode, size_t buf_sz)
{
	REX(TRN_FilterCreateStdFileFromUString(filename.mp_impl,(TRN_FilterStdFileOpenMode)open_mode,buf_sz,&m_impl));
	m_owner=true;
}
inline StdFile::StdFile (const char* filename, OpenMode open_mode, size_t buf_sz)
{
	REX(TRN_FilterCreateStdFileFromString(filename,(TRN_FilterStdFileOpenMode)open_mode,buf_sz,&m_impl));	
	m_owner=true;
}

#ifndef SWIG
inline StdFile::StdFile (FILE* stm, OpenMode open_mode, size_t buf_sz)
{
	REX(TRN_FilterCreateStdFileFromFile(stm,(TRN_FilterStdFileOpenMode)open_mode,buf_sz,&m_impl));
	m_owner=true;
}
#endif

inline bool StdFile::operator == (const StdFile& f) const
{
	TRN_Bool result;
	REX(TRN_FilterStdFileCompare(m_impl,f.m_impl,&result));
	return TBToB(result);
}

inline bool StdFile::Equivalent( const UString& ph1, const UString& ph2)
{
	TRN_Bool result;
	REX(TRN_PathCompare(ph1.mp_impl,ph2.mp_impl,&result));
	return TBToB(result);
}

inline size_t StdFile::FileSize()
{
	size_t result;
	REX(TRN_FilterStdFileFileSize(m_impl,&result));
	return result;
}

