package analix.DHIT.service;

import javax.mail.MessagingException;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    JavaMailSender mailSender;


    public void sendMail(String mailAddress) throws MessagingException, jakarta.mail.MessagingException {

        // メールに添付する「C:\text.txt」にあるファイルのオブジェクトを生成
//        String fileName = "text.txt";
//        FileSystemResource fileResource = new FileSystemResource("C:\\" + fileName);

        // メッセージクラス生成
        MimeMessage mimeMsg = mailSender.createMimeMessage();
        // メッセージ情報をセットするためのヘルパークラスを生成(添付ファイル使用時の第2引数はtrue)
        MimeMessageHelper helper = null;

        helper = new MimeMessageHelper(mimeMsg, true);

        /*
            送信元メールアドレスはapplication-development.propertiesに記述
        */

        // 送信先アドレスをセット
        helper.setTo(mailAddress);

        // 表題をセット
        helper.setSubject("メールタイトル");

        // 本文をセット
        helper.setText("メール本文わああああああああ");
        // 添付ファイルをセット
//        helper.addAttachment(fileName, fileResource);
        // メール送信
        mailSender.send(mimeMsg);

    }
}
