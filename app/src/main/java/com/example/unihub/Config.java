package com.example.unihub;

public class Config {

    // Zur Trennung von Layout und Code haben wir alle Konstanten des Codes in eine eigene Klasse getan
    // Strings des Layouts befinden sich in der strings.xml

    // MainActivity
    public static final int ADD_NEW_ENTRY_REQUEST_CODE = 0;
    public static final String ICON_FILE_NAME_SUFFIX = "_icon";


    // AddNewEntery
    public static final String MAIN_ACTIVITY_ITEM_NAME = "itemName";
    public static final String MAIN_ACTIVITY_ITEM_LECTURE = "itemLecture";
    public static final String MAIN_ACTIVITY_DOCUMENT_TYPE = "documentType";
    public static final String MAIN_ACTIVITY_ITEM_DATE = "itemDate";
    public static final int IMAGE_ICON_WIDTH_AND_HEIGHT = 100;
    public static final boolean IMAGE_ICON_SCALED_BITMAP_FILTER = true;
    public static final String DOCUMENT_TYPE_PRINTMEDIEN = "Printmedien";
    public static final String DOCUMENT_TYPE_MITSCHRIFT = "Mitschrift";
    public static final String DOCUMENT_TYPE_FORMULAR = "Formular";
    public static final String LIST_ITEM_CUSTOM_TEXT = "Bild ";
    public static final String TIME_STAMP_DATE_FORMAT_PATTERN = "yyyyMMdd_HHmmss";
    public static final String JPEG_IMAGE_NAME_PREFIX = "JPEG_";
    public static final String JPEG_FILE_SUFFIX = ".jpg";
    public static final String AUTHORITY = "com.example.unihub.android.fileprovider";
    public static final String TOAST_CONFIRM_DELETION_TEXT = "Bild wurde gelöscht!";


    //Toast text
    public static final String TOAST_WRONG_CHOICE_ERROR = "Bitte geben Sie genau einen Dokumententyp an!";
    public static final String TOAST_NO_NAME_ERROR = "Bitte geben Sie einen Dokumentennamen an!";
    public static final String TOAST_NO_IMAGES_ERROR = "Bitte mindestens ein Bild hinzufügen!";
    public static final String TOAST_NO_DATE_ERROR = "Bitte ein Datum hinzufügen!";
    public static final String TOAST_MORE_THAN_TWO_ENTRIES_MISSING = "Bitte fügen Sie mehr Eingaben hinzu!";


    //take picture and gallery
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;
    public static final String REMOVE_DIALOG_MESSAGE = "Sind Sie sicher, ob Sie diesen Eintrag löschen wollen?";
    public static final String REMOVE_DIALOG_TITLE = "Eintrag löschen";
    public static final String DATABASE_NAME = "ListEntryDatabase";


    // Actionbar Titles
    public static final String addNewEntryTitle = "Neuer Eintrag";


    // Directory for all images (internal storage)
    public static final String CUSTOM_DIR_FOR_IMAGES = "imageDir";


    // ImageViewer
    public static final String LIST_IMAGES_DEFAULT_TITLE_TEXT = "Bild ";
    public static final int JPEG_COMPRESS_QUALITY = 100;
    public static final String SHARE_FILE_PATH_CHILD_FOLDER = "images";
    public static final String SHARE_FILE_PATH_CHILD_PNG = "image.png";
    public static final String SHARE_FILE_PATH_CHILD_PNG_DIR = "/image.png";
    public static final String SHARE_FILE_PATH_PROVIDER = ".fileprovider";
    public static final String SHARE_IMAGE_FILE_TYPE = "image/png";
    public static final String SHARE_POP_UP_TEXT = "Wählen Sie eine App zum Öffnen...";


    // FingerPrintActivity
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String FINGERPRINT_SENSOR_ENABLED = "enabledFingerprintSensor";
    public static final String USER_WAS_ASKED_FOR_FINGERPRINT_SETTINGS = "userWasAskedForFingerPrintSettings";
    public static final String FINGERPRINT_REQUEST_MESSAGE_TEXT = "Wollen Sie Ihre Daten durch den Fingerabdrucksensor schützen";
    public static final String FINGERPRINT_REQUEST_TITLE = "Einstellung";
    public static final String FINGERPRINT_REQUEST_USE_TEXT = "Verwenden";
    public static final String FINGERPRINT_REQUEST_DO_NOT_USE_TEXT = "Nicht Verwenden";
    public static final String FINGERPRINT_ERROR_MESSAGE_CHANGE_IN_DEVICE_SETTIGS = "Bitte aktivieren Sie die Fingerabdruck-Berechtigung";
    public static final String FINGERPRINT_ERROR_MESSAGE_NO_FINGERPRINT_REGISTERED = "Kein Fingerabdruck konfiguriert. Bitte registrieren Sie mindestens einen Fingerabdruck in den Einstellungen Ihres Geräts";
    public static final String FINGERPRINT_ERROR_MESSAGE_EVEN_NO_PIN = "Bitte aktivieren Sie die Bildschirmsperre in den Einstellungen Ihres Geräts";
    public static final String FINGERPRINT_KEYSTORE_INSTANCE = "AndroidKeyStore";
    public static final String FINGERPRINT_FAILED_TO_GET_CIPHER = "Failed to get Cipher";
    public static final String FINGERPRINT_FAILED_TO_INIT_CIPHER = "Failed to init Cipher";
    public static final String FINGERPRINT_KEY_NAME = "yourKey";


    // AppSettings
    public static final String FINGERPRINT_ACTIVITY_TITLE = "Einstellungen";
    public static final String TOAST_NO_FINGERPRINT_SENSOR_TEXT = "Ihr Gerät besitzt keinen Fingerabdrucksensor";


    // FingerprintHandler
    public static final String FINGERPRINT_AUTHENTICATION_TEXT_ERROR = "Authentifizierungs error\n";
    public static final String FINGERPRINT_AUTHENTICATION_TEXT_FAILED = "Authentifizierung fehlgeschlagen";
    public static final String FINGERPRINT_AUTHENTICATION_TEXT_HELP = "Authentifizierung Hilfe\n";
    public static final String FINGERPRINT_AUTHENTICATION_TEXT_SUCCESS = "Authentifizierung erfolgreich!";


    // ImagesFolder
    public static final String KEY_FOLDER_TO_IMAGE_VIEWER_SELCTED_IMAGE_ID = "selectedImageId";
    public static final String KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM = "intentFrom";
    public static final String KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM_FOLDER_ADD_NEW_ENTRY = "AddNewEntry";
    public static final String KEY_FOLDER_TO_IMAGE_VIEWER_INTENTFROM_FOLDER_MAIN_ACTIVITY = "ImagesFolder";
    public static final String KEY_FOLDER_TO_IMAGE_VIEWER_ENTRY_NUMBER = "entryNumber";


    // ImagesFolder
    public static final String KEY_MAIN_ACTIVITY_TO_FOLDER_IMAGE_NUMBER = "imageNumber";
    public static final String KEY_MAIN_ACTIVITY_TO_FOLDER_AMOUNT_OF_IMAGES = "amountOfImages";
    public static final String KEY_MAIN_ACTIVITY_TO_FOLDER_ENTRY_NAME = "entryName";
    public static final String KEY_MAIN_ACTIVITY_TO_FOLDER_LECTURE_NAME = "lectureName";
}