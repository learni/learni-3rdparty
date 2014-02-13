
inline SearchResult::SearchResult(const SearchResult& b) 
{
	page_num = b.page_num;
	resCode = b.resCode;
	result = b.result;
	ambient = b.ambient;
	hlts = b.hlts;
}
inline bool SearchResult::IsFound() const {
	return resCode == e_found;
}
inline bool SearchResult::IsDocEnd() const {
	return resCode == e_done;
}
inline bool SearchResult::IsPageEnd() const {
	return resCode == e_page;
}
inline int SearchResult::GetPageNumber() const {
	return page_num;
}
inline UString SearchResult::GetMatch() const {
	return result;
}
inline UString SearchResult::GetAmbientString() const {
	return ambient;
} 
inline Highlights SearchResult::GetHighlights() const {
	return hlts;
}
inline SearchResult::SearchResult() {}

inline SearchResult::SearchResult(int p, TRN_TextSearchResultCode rc, UString r, UString a, Highlights h) 
{
	page_num = p; 
	resCode = rc; 
	result = r; 
	ambient = a; 
	hlts = h;
}

inline TextSearch::TextSearch()
{
	REX(TRN_TextSearchCreate(&mp_textsearch));
}

inline TextSearch::~TextSearch()
{
	REX(TRN_TextSearchDestroy(mp_textsearch));
	mp_textsearch = 0;
}

inline void TextSearch::Destroy()
{
	REX(TRN_TextSearchDestroy(mp_textsearch));
	mp_textsearch = 0;
}

inline bool TextSearch::Begin(PDFDoc& doc, const UString& pattern, TRN_UInt32 mode, int start_page, int end_page)
{
	RetBool( TRN_TextSearchBegin(mp_textsearch, (TRN_PDFDoc)doc.mp_doc, pattern.mp_impl, mode, start_page, end_page, &result) );
}

inline SearchResult TextSearch::Run()
{
	TRN_TextSearchResultCode code;
	UString result_str;
	UString ambient_str;
	int page_num;
	Highlights hlts;
	REX(TRN_TextSearchRun(mp_textsearch, &page_num, result_str.mp_impl, ambient_str.mp_impl, hlts.mp_highlights, &code));
	return SearchResult(page_num, code, result_str, ambient_str, hlts);
}

inline bool TextSearch::SetPattern(const UString& pattern)
{
	RetBool(TRN_TextSearchSetPattern(mp_textsearch, pattern.mp_impl, &result));
}

inline TextSearch::Mode TextSearch::GetMode() const
{
	TRN_UInt32 mode;
	REX(TRN_TextSearchGetMode(mp_textsearch, &mode));
	return (TextSearch::Mode)mode;
}

inline void TextSearch::SetMode( Mode mode )
{
	REX(TRN_TextSearchSetMode(mp_textsearch, (TRN_UInt32)mode));
}

inline int TextSearch::GetCurrentPage() const
{
	int page;
	REX(TRN_TextSearchGetCurrentPage(mp_textsearch, &page));
	return page;
}
