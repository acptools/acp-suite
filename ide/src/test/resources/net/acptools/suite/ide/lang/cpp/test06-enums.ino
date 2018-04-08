// Command codes
enum CommandCode: byte {
  RESET = 1,
  SET_KEY = 2,
  READ_BLOCK = 3,
  WRITE_BLOCK = 4,
  READ_SECTOR_TRAILER = 5,
  WRITE_SECTOR_TRAILER = 6
};

// Codes of messages sent by the reader
enum ReaderMsgCode: byte {
  COMMAND_OK = 1,
  COMMAND_FAILED = 2,
  CARD_DETECTED = 3,
  CARD_REMOVED = 4
};

// Codes of messages sent by the reader
enum CardType: byte {
    UNKNOWN = 0,
    PICC_TYPE_ISO_14443_4 = 1,
    PICC_TYPE_ISO_18092 = 2,
    PICC_TYPE_MIFARE_MINI = 3,
    PICC_TYPE_MIFARE_1K = 4,
    PICC_TYPE_MIFARE_4K = 5,
    PICC_TYPE_MIFARE_UL = 6,
    PICC_TYPE_MIFARE_PLUS = 7,
    PICC_TYPE_TNP3XXX = 8
};

// Type of key
enum KeyType: byte {
  NONE = 0,
  KEY_A = 1,
  KEY_B = 2
};
