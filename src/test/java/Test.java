import de.craftsblock.craftscore.buildin.config.Config;
import de.craftsblock.craftscore.buildin.config.ConfigParser;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Test {

    public static void main(String[] args) {
        int current = 0;
        ConcurrentLinkedQueue<String> strings = new ConcurrentLinkedQueue<>();
        a(strings,

                "Tot durch Tripper",
                "Realschulabschluss",
                "Eine tolle Studienzeit",
                "Lange Freundschaften",
                "Guter Job",
                "Gesunde Katze",
                "Guten Schulabschluss",
                "Gute Noten",
                "Gute Zeugnisse",
                "Guten Job",
                "Lange Freundschaften",
                "Heile Familie",
                "PC kaputt machen",
                "Frei Stunde",
                "Was geiles Essen",
                "LSD - Trip",
                "Guten Schulabschluss",
                "Mercedes G-Klasse AMG",
                "Guten Schulabschluss",
                "Einen schönen Garten",
                "Viel Geld für eine Weltreise",
                "Informatik Studium",
                "Gut Arbeiten",
                "Ende Internationaler Krisen",
                "Führerschein machen",
                "Dämon beschwören",
                "Guten Job",
                "Neue Klamotten anziehen",
                "Geld verdienen und meine Familie unterstützen",
                "RTX 3090 TI haben",
                "PC Upgrade",
                "Laptop zerstören",
                "Große Wohnung / Haus",
                "Eine Zukunft",
                "Alle interessanten Mangas",
                "Fifa Pack Luck",
                "Satanismus beitreten",
                "Blowjob bekommen",
                "Blowjob geben",
                "Immunität gegen alle Krankheiten",
                "Wirtschaftsstudium",
                "Genug Geld zum Schulden ausgleichen",
                "Soziale und Nachhaltige Gesellschaft",
                "1000€ Messer Skin in CSGO",
                "Schule schwänzen",
                "An überdosis Heroin sterben",
                "Neue Menschen treffen",
                "Rotewelle",
                "Neues Handy",
                "Ein neuer PC",
                "Ein laptop in der Schule zum Zocken",
                "PC upgrade",
                "Das es meinen Mitarbeitern gut geht",
                "Genug Essen",
                "Mathetest bestehen",
                "Gratis monat im Stripclub",
                "Mit Freunden treffen",
                "Nach Hause gehen",
                "100% Tesla Aktien",
                "Schlafen",
                "Junkie werden und abstürzen",
                "Eine 4 TB SSD",
                "2 PC Upgrades",
                "Enorme Wirtschaftsprobleme gelöst",
                "Einen guten Laptop",
                "Erfolgreiche Karriere",
                "Hauptschulabschluss",
                "Mein Traumjob",
                "Erfolg haben",
                "Glückwunsch du bist jetzt geboren",
                "Abi",
                "Genug Trinken",
                "Aids bekommen",
                "Abitur",
                "Ernährungsstudium",
                "Tot",
                "ein großes Haus",
                "Eine eigene Firma haben",
                "Neues Headset",
                "Neue Grafikkarte",
                "Ein Studium",
                "Grünewelle");
        Config config = (Config) new ConfigParser().parse("{}");
        assert config != null;

        config.set("utils.cards.size", strings.size());
        config.set("utils.cards.last", strings.size() - 1);
        config.set("utils.cards.first", 0);

        config.set("utils.extras.0", "nothing");
        config.set("utils.extras.1", "death");
        config.set("utils.extras.2", "pick");
        config.set("utils.extras.3", "pick2");
        config.set("utils.extras.4", "freeze");
        config.set("utils.extras.5", "turn");

        for (String s : strings) {
            config.set("card." + current + ".name", s);
            config.set("card." + current + ".extra", 0);
            ++current;
        }
        System.out.println(config.asString());
    }

    public static void a(ConcurrentLinkedQueue<String> list, String... strings) {
        list.addAll(Arrays.asList(strings));
    }

}
