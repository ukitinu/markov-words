package ukitinu.markovwords;

import ukitinu.markovwords.models.Dict;
import ukitinu.markovwords.models.Gram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ukitinu.markovwords.AlphabetUtils.WORD_END;

public final class Main {
    private static final Dict DICT = new Dict("berlusca", AlphabetUtils.getAsciiSimple());
    private static final Map<String, Gram> GRAM_MAP = new HashMap<>();

    private Main() {
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        populateDict(TEXT_IN);

        for (int i = 0; i < 10; i++) {
            printWord(GRAM_MAP.get("B"), "Berlusco");
        }

        long delta = System.currentTimeMillis() - start;
        System.out.printf("%d.%d", delta / 1000, delta % 1000);
    }

    private static void printWord(Gram starter, String prefix) {
        String randomWord;
        int tries = 0;
        do {
            randomWord = createWord(starter);
            tries++;
        } while (!randomWord.startsWith(prefix));
        System.out.printf("%8d - %s\n", tries, randomWord);
    }

    private static void populateDict(String textIn) {
        initGrams();
        String clean = cleanText(textIn, DICT);
        ingestText(clean, GRAM_MAP);
    }

    private static String createWord(Gram gram) {
        var word = new StringBuilder(gram.getGram());
        char next = gram.next();
        while (next != WORD_END) {
            word.append(next);
            var nextGram = GRAM_MAP.get(String.valueOf(next));
            next = nextGram.next();
        }
        return word.toString();
    }

    //region populate-dict
    private static void initGrams() {
        for (char letter : DICT.alphabet()) {
            Gram gram = new Gram(String.valueOf(letter), DICT);
            GRAM_MAP.put(String.valueOf(letter), gram);
        }
    }

    private static void ingestText(String text, Map<String, Gram> gramMap) {
        String current = String.valueOf(WORD_END);
        for (char letter : text.toCharArray()) {
            Gram gram = gramMap.get(current);
            gram.increment(letter);
            current = String.valueOf(letter);
        }
        Gram gram = gramMap.get(current);
        gram.increment(WORD_END);
    }


    private static String cleanText(String text, Dict dict) {
        List<Character> list = new ArrayList<>();


        for (char letter : text.toCharArray()) {
            if (dict.alphabet().contains(letter)) {
                list.add(letter);
            } else {
                if (!list.isEmpty() && list.get(list.size() - 1) != WORD_END) {
                    list.add(WORD_END);
                }
            }
        }

        if (!list.isEmpty() && list.get(list.size() - 1) == WORD_END) list.remove(list.size() - 1);

        return list.stream().map(String::valueOf).collect(Collectors.joining());
    }
    //endregion


    //region text-in
    private static final String TEXT_IN = """ 
            Il centrodestra in conclave indica la candidatura di Silvio Berlusconi per il Quirinale invitandolo a
            "sciogliere la riserva", una decisione che irrita i Dem ed M5s.
            Le prime reazioni "a caldo" del Nazareno al comunicato congiunto del centrodestra sono: delusione
            per il merito e preoccupazione per le implicazioni che questa scelta può avere.
            È quanto fanno sapere fonti del Nazareno.
            "Silvio Berlusconi alla Presidenza della Repubblica è per noi un'opzione irricevibile e improponibile.
            Il centrodestra non blocchi l'Italia.
            Qui fuori c'è un Paese che soffre e attende risposte, non possiamo giocare sulle spalle di famiglie e imprese".
            Così su Twitter il leader M5S Giuseppe Conte dopo il vertice del centrodestra sul Quirinale.
            "Il centrodestra, che rappresenta la maggioranza relativa nell'assemblea chiamata a eleggere il nuovo
            capo dello Stato, ha il diritto e il dovere di proporre la candidatura al massimo vertice delle istituzioni.
            I leader della coalizione hanno convenuto che Silvio Berlusconi sia la figura adatta a ricoprire in questo
            frangente difficile l'alta carica con l'autorevolezza e l'esperienza che il Paese merita e che gli italiani
            si attendono. Gli chiedono pertanto di sciogliere in senso favorevole la riserva fin qui mantenuta".
            È quanto si legge in una nota congiunta delle forze di centrodestra al termine del vertice svolto a Roma.
            La riunione si è tenuta a Villa Grande.
            Insieme all'ex premier i leader di Lega e Fratelli d'Italia, Matteo Salvini e Giorgia Meloni e il presidente
            di Coraggio italia, Luigi Brugnaro. Presente anche Gianni Letta.
            "Le forze politiche del centrodestra lavoreranno per trovare le più ampie convergenze in Parlamento e
            chiedono altresì ai presidenti di Camera e Senato di assumere tutte le iniziative atte a garantire per tutti
            i 1009 grandi elettori l'esercizio del diritto costituzionale al voto".
            Secondo fonti del centrodestra Silvio Berlusconi farà una verifica sui numeri in vista di un'eventuale
            sua corsa alla presidenza della Repubblica.
            Un nuovo vertice si dovrebbe tenere a metà della prossima settimana.
            In un clima sinceramente cordiale Matteo Salvini ha chiesto esplicitamente a Berlusconi di sciogliere
            la riserva, se ritiene, e di dare rassicurazioni su numeri e soprattutto nomi dei grandi elettori che
            dovrebbero sostenerlo, specialmente al di fuori del centrodestra.
            È quanto trapela da fonti leghiste al termine del vertice di centrodestra.
            Il leader della Lega si è presentato con il dettaglio storico degli ultimi presidenti della Repubblica,
            da Scalfaro a Mattarella, per ribadire quanti voti hanno preso e in quale votazione.
            Un modo per evidenziare - riferiscono le stesse fonti - la delicatezza della partita e i numeri
            necessari per portare a casa il risultato. "Il centrodestra deve essere compatto dall'inizio alla fine,
            qualunque cosa accada" ha assicurato Salvini.
            Ora si attendono risposte da Berlusconi.
            La settimana prossima sarà densa di trattative: Salvini infine ha confidato di essere in contatto
            da tempo con tutti i leader con l'ambizione di svelenire il clima.
            Per la Lega, Draghi deve continuare a fare il premier.
            "Ripeto quello che ho sempre detto, il candidato deve essere unitario e non divisivo.
            Non deve essere un capo politico, ma una figura istituzionale".
            Lo ha detto Enrico Letta commentando l'indicazione di Silvio Berlusconi candidato al Quirinale per il centrodestra.
            "Il clima di serenità e di valutazione degli interessi generali del bene comune, prima di tutto,
            deve essere la guida per tutti quelli che hanno la responsabilità, il compito di eleggere il capo dello Stato.
            Spero che si possa svolgere in questo clima di serena partecipazione, di armonia, di impegno comune per il bene comune",
            ha detto Letta lasciando la basilica di Santa Maria degli Angeli dopo il funerale di David Sassoli.
            "Dopo 30 anni il centrodestra ha una occasione storica di fare una scelta di assoluto livello che
            non sia necessariamente di sinistra.
            Per 30 anni è stata la sinistra a dare le carte, diciamo che dopo 30 anni anche al Quirinale il
            centrodestra penso che abbia tutti i titoli, oltre che i numeri per fare la sua partita.
            Nessuno può dire 'Berlusconi no, tu no' perché è stato Presidente del Consiglio per tre volte.
            Io sono al lavoro perché voglio dare agli italiani una immagine di efficienza",
            ha detto in mattinata Salvini al Villaggio Olimpico a Roma a margine di una iniziativa elettorale a
            sostegno di Simonetta Matone in vista delle supplitive.""";
    //endregion


}
