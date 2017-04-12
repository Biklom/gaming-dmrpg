
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import org.junit.Assert;
import org.wikipedia.Wiki;

public class TestWikiJava {

    @org.junit.Test
    public void doTest2() {
        Wiki w = new Wiki("dmrpg.shoutwiki.com/");
        w.setMarkBot(true);
        try {
            w.login("biklom", "");
            String[] pages = w.listPages("", null, Wiki.MAIN_NAMESPACE);
//            for (String s : pages) {
//                System.out.println(s);
//            }
            System.out.println(pages.length + " pages on wiki");
            pages = w.listPages("", null, Wiki.FILE_NAMESPACE);
//            for (String s : pages) {
//                System.out.println(s);
//            }
            w.logout();
            System.out.println(pages.length + " medias on wiki");
        } catch (IOException | FailedLoginException ex) {
            Logger.getLogger(TestWikiJava.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail();
        } catch (LoginException ex) {
            Logger.getLogger(TestWikiJava.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @org.junit.Test
    public void doTest1() {
//        Wiki w = new Wiki("dmrpg.shoutwiki.com/");
//        w.setMarkBot(false);
//        try {
//            w.login("biklom", "");
//            String mediaPath = "F:\\dungeon monsters rpg\\Content\\Monsters";
//            File mdir = new File(mediaPath);
//            File cards = new File(mdir, "Cards");
//            File sprites = new File(mdir, "Sprites");
//            for (File f : cards.listFiles()) {
//                w.upload(f, "File:"+f.getName(), "", "");
//                System.out.println(f.getName());
//            }
//            for (File f : sprites.listFiles()) {
//                w.upload(f, f.getName(), "", "");
//                System.out.println(f.getName());
//            }
//            w.logout();
//        } catch (IOException | FailedLoginException ex) {
//            Logger.getLogger(TestWikiJava.class.getName()).log(Level.SEVERE, null, ex);
//            Assert.fail();
//        } catch (LoginException ex) {
//            Logger.getLogger(TestWikiJava.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
