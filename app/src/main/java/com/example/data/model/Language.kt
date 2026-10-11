package com.example.data.model

enum class IndianLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val tagline: String,
    val greeting: String,
    val sosLabel: String,
    val continueLabel: String,
    val sampleDialect: String
) {
    ENGLISH(
        code = "en",
        englishName = "English",
        nativeName = "English",
        tagline = "Your Safety, Our Priority",
        greeting = "Welcome",
        sosLabel = "SOS EMERGENCY",
        continueLabel = "CONTINUE",
        sampleDialect = "Pan-India National Default"
    ),
    HINDI(
        code = "hi",
        englishName = "Hindi",
        nativeName = "हिन्दी",
        tagline = "आपकी सुरक्षा, हमारी प्राथमिकता",
        greeting = "नमस्ते",
        sosLabel = "आपातकालीन SOS",
        continueLabel = "आगे बढ़ें (जारी रखें)",
        sampleDialect = "उत्तर और मध्य भारत (North & Central India)"
    ),
    BENGALI(
        code = "bn",
        englishName = "Bengali",
        nativeName = "বাংলা",
        tagline = "আপনার সুরক্ষা, আমাদের অগ্রাধিকার",
        greeting = "নমস্কার",
        sosLabel = "জরুরী SOS",
        continueLabel = "এগিয়ে যান",
        sampleDialect = "পশ্চিমবঙ্গ ও ত্রিপুরা (West Bengal & Tripura)"
    ),
    TAMIL(
        code = "ta",
        englishName = "Tamil",
        nativeName = "தமிழ்",
        tagline = "உங்கள் பாதுகாப்பு, எங்கள் முன்னுரிமை",
        greeting = "வணக்கம்",
        sosLabel = "அவசர SOS",
        continueLabel = "தொடரவும்",
        sampleDialect = "தமிழ்நாடு (Tamil Nadu)"
    ),
    TELUGU(
        code = "te",
        englishName = "Telugu",
        nativeName = "తెలుగు",
        tagline = "మీ భద్రత, మా ప్రాధాన్యత",
        greeting = "నమస్కారం",
        sosLabel = "అత్యవసర SOS",
        continueLabel = "కొనసాగించండి",
        sampleDialect = "ఆంధ్రప్రదేశ్ & తెలంగాణ (AP & Telangana)"
    ),
    MARATHI(
        code = "mr",
        englishName = "Marathi",
        nativeName = "मराठी",
        tagline = "तुमची सुरक्षितता, आमचे प्राधान्य",
        greeting = "नमस्कार",
        sosLabel = "तातडीची मदत (SOS)",
        continueLabel = "पुढे जा",
        sampleDialect = "महाराष्ट्र (Maharashtra)"
    ),
    GUJARATI(
        code = "gu",
        englishName = "Gujarati",
        nativeName = "ગુજરાતી",
        tagline = "તમારી સુરક્ષા, અમારી પ્રાથમિકતા",
        greeting = "નમસ્તે",
        sosLabel = "કટોકટી SOS",
        continueLabel = "આગળ વધો",
        sampleDialect = "ગુજરાત (Gujarat)"
    ),
    KANNADA(
        code = "kn",
        englishName = "Kannada",
        nativeName = "ಕನ್ನಡ",
        tagline = "ನಿಮ್ಮ ಸುರಕ್ಷತೆ, ನಮ್ಮ ಆದ್ಯತೆ",
        greeting = "ನಮಸ್ಕಾರ",
        sosLabel = "ತುರ್ತು SOS",
        continueLabel = "ಮುಂದುವರಿಯಿರಿ",
        sampleDialect = "ಕರ್ನಾಟಕ (Karnataka)"
    ),
    MALAYALAM(
        code = "ml",
        englishName = "Malayalam",
        nativeName = "മലയാളം",
        tagline = "നിങ്ങളുടെ സുരക്ഷ, ഞങ്ങളുടെ മുൻഗണന",
        greeting = "നമസ്കാരം",
        sosLabel = "അടിയന്തര SOS",
        continueLabel = "തുടരുക",
        sampleDialect = "കേരളം (Kerala)"
    ),
    PUNJABI(
        code = "pa",
        englishName = "Punjabi",
        nativeName = "ਪੰਜਾਬੀ",
        tagline = "ਤੁਹਾਡੀ ਸੁਰੱਖਿਆ, ਸਾਡੀ ਪਹਿਲ",
        greeting = "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ",
        sosLabel = "ਐਮਰਜੈਂਸੀ SOS",
        continueLabel = "ਅੱਗੇ ਵਧੋ",
        sampleDialect = "ਪੰਜਾਬ (Punjab)"
    )
}
