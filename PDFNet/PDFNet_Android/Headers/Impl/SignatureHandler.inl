#ifndef INL_CPPSDFSignatureHandler
#define INL_CPPSDFSignatureHandler
#include <C/Common/TRN_UString.h>

#ifdef SWIG
#ifndef TRN_SIGAPI
#define TRN_SIGAPI __stdcall
#endif
#endif

namespace pdftron {
namespace SDF {

inline UString SignatureHandler::GetName() const
{
    throw (pdftron::Common::Exception("pdftron::SDF::SignatureHandler::GetName not implemented.", __LINE__, __FILE__, __FUNCTION__, "pdftron::SDF::SignatureHandler::GetName not implemented."));
}

inline void SignatureHandler::AppendData(const std::vector<pdftron::UInt8>& in_data)
{
    throw (pdftron::Common::Exception("pdftron::SDF::SignatureHandler::AppendData not implemented.", __LINE__, __FILE__, __FUNCTION__, "pdftron::SDF::SignatureHandler::AppendData not implemented."));
}

inline bool SignatureHandler::Reset()
{
    throw (pdftron::Common::Exception("pdftron::SDF::SignatureHandler::Reset not implemented.", __LINE__, __FILE__, __FUNCTION__, "pdftron::SDF::SignatureHandler::Reset not implemented."));
}

inline std::vector<pdftron::UInt8> SignatureHandler::CreateSignature()
{
    throw (pdftron::Common::Exception("pdftron::SDF::SignatureHandler::CreateSignature not implemented.", __LINE__, __FILE__, __FUNCTION__, "pdftron::SDF::SignatureHandler::Generate not implemented."));
}
/*
inline SignatureHandler::ValidateSignatureResult SignatureHandler::ValidateSignature(const SDF::Obj& in_sig_dict)
{
    throw (pdftron::Common::Exception("pdftron::SDF::SignatureHandler::ValidateSignature not implemented.", __LINE__, __FILE__, __FUNCTION__, "pdftron::SDF::SignatureHandler::Generate not implemented."));
}
*/
inline SignatureHandler::~SignatureHandler()
{
}
#ifndef SWIGHIDDEN_SIG

#define CREATE_PDFNETEX(message) pdftron::Common::Exception("Exception", __LINE__, __FILE__, __FUNCTION__, message)
#define SIGAPI_BEX try{
#define SIGAPI_EEX }catch(pdftron::Common::Exception& e){throw(e);}catch(std::exception& e){throw(CREATE_PDFNETEX(e.what()));}catch(std::string& e){throw (CREATE_PDFNETEX(e.c_str()));}catch(const char * e){throw(CREATE_PDFNETEX(e));}catch(...){throw(CREATE_PDFNETEX("Unknown exception"));}
                    
inline void TRN_SIGAPI SignatureHandler::TRN_SignatureHandlerGetNameImpl(TRN_UString* in_sighandler, void* derived)
{
    SIGAPI_BEX;
    if (derived == NULL)
        throw (pdftron::Common::Exception("derived == NULL", __LINE__, __FILE__, __FUNCTION__, "Failed to obtain derived instance of pdftron::SDF::SignatureHandler."));

    if (in_sighandler == NULL)
        return;

    UString temp = ((SignatureHandler*) derived)->GetName();
    REX(TRN_UStringCopy(temp.mp_impl, in_sighandler));
    SIGAPI_EEX;
}

inline void TRN_SIGAPI SignatureHandler::TRN_SignatureHandlerAppendDataImpl(const TRN_SignatureData in_data, void* derived)
{
    SIGAPI_BEX;
    if (derived == NULL)
        throw (pdftron::Common::Exception("derived == NULL", __LINE__, __FILE__, __FUNCTION__, "Failed to obtain derived instance of pdftron::SDF::SignatureHandler."));

    std::vector<UInt8> dataToAppend;
    dataToAppend.resize(in_data.length);
    memcpy(&(dataToAppend[0]), in_data.data, in_data.length);
    ((SignatureHandler*) derived)->AppendData(dataToAppend);
    SIGAPI_EEX;
}

inline TRN_Bool TRN_SIGAPI SignatureHandler::TRN_SignatureHandlerResetImpl(void* derived)
{
    SIGAPI_BEX;
    if (derived == NULL)
        throw (pdftron::Common::Exception("derived == NULL", __LINE__, __FILE__, __FUNCTION__, "Failed to obtain derived instance of pdftron::SDF::SignatureHandler."));

    return (BToTB(((SignatureHandler*) derived)->Reset()));
    SIGAPI_EEX;
}

inline TRN_SignatureData TRN_SIGAPI SignatureHandler::TRN_SignatureHandlerCreateSignatureImpl(void* derived)
{
    SIGAPI_BEX;
    if (derived == NULL)
        throw (pdftron::Common::Exception("derived == NULL", __LINE__, __FILE__, __FUNCTION__, "Failed to obtain derived instance of pdftron::SDF::SignatureHandler."));

    TRN_SignatureData result;
    SignatureHandler* sig = ((SignatureHandler*) derived);
    sig->m_signature_data = sig->CreateSignature();
    
    result.data = &(sig->m_signature_data[0]);
    result.length = sig->m_signature_data.size();
    return result;
    SIGAPI_EEX;
}
/*
inline TRN_ValidateSignatureResult TRN_SIGAPI SignatureHandler::TRN_SignatureHandlerValidateSignatureImpl(const TRN_Obj in_sig_dict, void* derived)
{
    SIGAPI_BEX;
    if (derived == NULL)
        throw (pdftron::Common::Exception("derived == NULL", __LINE__, __FILE__, __FUNCTION__, "Failed to obtain derived instance of pdftron::SDF::SignatureHandler."));

    SignatureHandler* sig = ((SignatureHandler*) derived);
    pdftron::SDF::Obj sigDict(in_sig_dict);
    ValidateSignatureResult vresult = sig->ValidateSignature(sigDict);
    TRN_ValidateSignatureResult result;
    result.valid = BToTB(vresult.m_valid);
    result.wrong_handler = BToTB(vresult.m_wronghandler);
    result.error_code = vresult.m_errorcode;
    //TODO: allocate memory for message and assign back the message
    return result;
    SIGAPI_EEX;
}
*/
inline void TRN_SIGAPI SignatureHandler::TRN_SignatureHandlerDestroyImpl(void* derived)
{
#ifndef SWIG
    // NOTE on SWIG:
    // we let the target language's garbage collector do the job instead (if we do this ourselves, there are seldom
    // crashes because GC is attempting to free invalid memory...
    SIGAPI_BEX;
    if (derived != NULL) {
        SignatureHandler* sh = (SignatureHandler*) derived;
        delete (sh);
    }
    SIGAPI_EEX;
#endif // SWIG
}
#endif // SWIGHIDDEN_SIG

}; // namespace SDF
}; // namespace pdftron
#endif // INL_CPPSDFSignatureHandler
