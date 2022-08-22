import de.craftsblock.craftscore.buildin.config.Config;
import de.craftsblock.craftscore.buildin.config.ConfigParser;

public class Test {

    public static void main(String[] args) {
        Config conf = (Config) new ConfigParser().parse("{}");
        conf.set("version.1\\.0", "Test");
        System.out.println(conf.asString());
    }

}
