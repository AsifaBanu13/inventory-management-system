package util;

import java.io.File;

public class EmailUtil {
    public static void sendReport(String recepient,String subject, String message,String filePath){
        File file=new File(filePath);
        if(!file.exists()){
            System.out.println("Report file not found"+filePath);
            return;
        }

        System.out.println("------------------------------------------------");
        System.out.println("📤 Sending Email To: " + recepient);
        System.out.println("✉️  Subject: " + subject);
        System.out.println("📝 Message: " + message);
        System.out.println("📎 Attachment: " + file.getName());
        System.out.println("✅ Email sent Successfully (Simulation) 🎉");
        System.out.println("------------------------------------------------");
    }

}
