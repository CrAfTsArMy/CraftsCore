import de.craftsarmy.craftscore.utils.Touch;

public class Test {

    public static void main(String[] args) throws Exception {
        Touch<Touching> touch = new Touch<>(Test.class);
        touch.touch(Touching.class);
    }

    public void callback() {
        System.out.println("Callback");
    }

}
