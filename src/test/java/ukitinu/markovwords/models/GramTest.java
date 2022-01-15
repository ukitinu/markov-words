package ukitinu.markovwords.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GramTest {
    private Dict dict;
    private Gram gram;

    @BeforeEach
    void setUp() {
        dict = new Dict("dict", Set.of('a', 'b', 'c'));

        gram = new Gram("gram", dict);
    }

    @Test
    void isEmpty() {
        assertTrue(gram.isEmpty());

        gram.increment('a');
        assertFalse(gram.isEmpty());
    }

    @Test
    void get() {
        assertEquals(0, gram.get('z'));

        gram.increment('a');
        gram.increment('a');
        gram.increment('a');
        assertEquals(3, gram.get('a'));

        assertEquals(0, gram.get('c'));
    }

    @Test
    void increment() {
        var e = assertThrows(IllegalArgumentException.class, () -> gram.increment('z'));
        assertEquals("z not in alphabet", e.getMessage());

        assertEquals(0, gram.get('b'));
        assertDoesNotThrow(() -> gram.increment('b'));
        assertDoesNotThrow(() -> gram.increment('b'));
        assertEquals(2, gram.get('b'));
    }

    @Test
    void next() {
        char nextWhenEmpty = assertDoesNotThrow(() -> gram.next());
        assertEquals('_', nextWhenEmpty);

        gram.increment('c');
        assertEquals('c', gram.next());

        // is there a way to test a random choice different from this?
        gram.increment('a');
        for (int i = 0; i < 20; i++) {
            assertTrue(Set.of('a', 'c').contains(gram.next()));
        }
    }

    @Test
    void add() {
        var gramDictName = new Gram("gram", new Dict("new-dict", dict.alphabet()));
        var e = assertThrows(IllegalArgumentException.class, () -> gram.add(gramDictName));
        assertEquals("trying to add n-grams from different lexicons", e.getMessage());

        var gramDictAlph = new Gram("gram", new Dict(dict.name(), Set.of()));
        assertThrows(IllegalArgumentException.class, () -> gram.add(gramDictAlph));

        var gramName = new Gram("different", dict);
        e = assertThrows(IllegalArgumentException.class, () -> gram.add(gramName));
        assertEquals("trying to add different n-grams", e.getMessage());

        var gramEq = new Gram("gram", dict);
        assertDoesNotThrow(() -> gram.add(gramEq));
        assertDoesNotThrow(() -> gram.add(gram));

        gram.increment('a');
        gramEq.increment('a');
        gramEq.increment('b');
        gram.add(gramEq);

        assertEquals(2, gram.get('a'));
        assertEquals(1, gram.get('b'));
    }
}