package ukitinu.markovwords;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ukitinu.markovwords.AlphabetUtils.WORD_END;
import static ukitinu.markovwords.Validator.DICT_DESC_MAX_LEN;

class ValidatorTest {

    @Test
    void validateDictName() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictName(null));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictName("  \t\n"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictName("_invalid"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictName("-also-invalid"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictName("9this-is-not"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictName("Invalid!"));

        assertDoesNotThrow(() -> Validator.validateDictName("GoodName101"));
        assertDoesNotThrow(() -> Validator.validateDictName("This-isOk-"));
    }

    @Test
    void validateDictDesc() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictDesc(null));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictDesc("a".repeat(DICT_DESC_MAX_LEN + 1)));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictDesc("\tTabs are not allowed!"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictDesc("Tabs\tare not allowed!"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictDesc("Newlines\nare\r\nnot\rallowed!"));

        assertDoesNotThrow(() -> Validator.validateDictDesc("a".repeat(DICT_DESC_MAX_LEN)));
        assertDoesNotThrow(() -> Validator.validateDictDesc("  "));
        assertDoesNotThrow(() -> Validator.validateDictDesc("***This is, (!) 1. -great- _dict_ DESCription!?!***"));
    }

    @Test
    void validateDictAlphabet() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictAlphabet(null));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictAlphabet("\t"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictAlphabet("\n"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictAlphabet("\r"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictAlphabet("abc" + WORD_END));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictAlphabet("\u001D"));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateDictAlphabet("\u009C"));

        assertDoesNotThrow(() -> Validator.validateDictAlphabet("aA7&%!|'\"à.,-<ç$@€"));
    }
}