inline Flattener::Flattener()
{
	REX(TRN_FlattenerCreate(&mp_impl));
}

inline Flattener::~Flattener()
{
	Destroy();
}

inline void Flattener::SetDPI(UInt32 dpi)
{
	REX(TRN_FlattenerSetDPI(mp_impl, dpi));
}

inline void Flattener::SetMaximumImagePixels(UInt32 max_pixels)
{
	REX(TRN_FlattenerSetMaximumImagePixels(mp_impl, max_pixels));
}

inline void Flattener::SetPreferJPG(bool jpg)
{
	REX(TRN_FlattenerSetPreferJPG(mp_impl, BToTB(jpg)));
}

inline void Flattener::SetThreshold(enum Threshold threshold)
{
    REX(TRN_FlattenerSetThreshold(mp_impl, static_cast<TRN_FlattenThreshold>(threshold)));
}

inline void Flattener::Process(PDFDoc& doc, enum FlattenMode mode )
{
	REX(TRN_FlattenerProcess(mp_impl, doc.mp_doc, static_cast<TRN_FlattenMode>(mode)));
};

inline void Flattener::Destroy()
{
	REX(TRN_FlattenerDestroy(mp_impl));
	mp_impl=0;
}
