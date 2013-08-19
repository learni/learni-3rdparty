#ifndef CPPRedactor_INL
#define CPPRedactor_INL

#include <cassert>

namespace pdftron {
	namespace PDF {

inline Redactor::Redaction::Redaction(int page_num,
	const Rect& bbox,
	bool negative,
	const UString& text)
{
	REX(TRN_Redactor_RedactionCreate(&mp_imp,
		page_num,
		(const TRN_Rect*)&bbox,
		BToTB(negative),
		text.mp_impl));
}

inline Redactor::Redaction::~Redaction() {
	REX(TRN_Redactor_RedactionDestroy(mp_imp));
	mp_imp=0;
}

inline Redactor::Redaction::Redaction(TRN_Redaction impl) : mp_imp(impl) { }

inline void Redactor::Redaction::Destroy() {
	REX(TRN_Redactor_RedactionDestroy(mp_imp));
	mp_imp=0;
}

inline Redactor::Redaction::Redaction(const Redaction& other) {
	REX(TRN_Redactor_RedactionCopy(&mp_imp, other.mp_imp));
}

inline void Redactor::Redact(PDFDoc& doc, const std::vector<Redaction>& red_arr, const Appearance& app, bool ext_neg_mode, bool page_coord_sys)
{
	TRN_RedactionAppearance trn_app;
	REX(TRN_Redactor_AppearanceCreate(&trn_app,
		BToTB(app.RedactionOverlay),
		&app.PositiveOverlayColor.m_c,
		&app.NegativeOverlayColor.m_c,
		BToTB(app.Border),
		BToTB(app.UseOverlayText),
		app.TextFont.mp_font,
		app.MinFontSize,
		app.MaxFontSize,
		&app.TextColor.m_c,
		app.HorizTextAlignment,
		app.VertTextAlignment,
		BToTB(app.ShowRedactedContentRegions),
		&app.RedactedContentColor.m_c));

	TRN_Exception ret = TRN_Redactor_Redact(doc.mp_doc, (TRN_Redaction*)&(red_arr[0]), red_arr.size(), trn_app, BToTB(ext_neg_mode), BToTB(page_coord_sys));
	REX(TRN_Redactor_AppearanceDestroy(trn_app));

	if(ret!=0) throw Common::Exception(ret);
}

#ifdef SWIG
inline Redactor::Redaction::Redaction():mp_imp(0) {}
#endif

	}; // namespace PDF
}; // namespace pdftron

#endif // CPPRedactor_INL
