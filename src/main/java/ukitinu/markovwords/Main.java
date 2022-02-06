package ukitinu.markovwords;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (args != null) {
            if (args.length > 0) {
                String s = "";
                for(char c : args[0].toCharArray()) {
                    s += c;
                }
                System.out.println(s);
            }
        }
    }


}
