# Business-reporting-system


## ローカル開発

* ローカルDBへの接続設定  
以下設定ファイルを参考にapplication-development.propertiesを作成してください。
src/main/resources/application-development-sample.properties

```
spring.datasource.url=jdbc:mysql://localhost:XXXX/database_name
spring.datasource.username=hoge
spring.datasource.password=hogehoge
```

* ローカルでアプリを実行する場合の注意点  
コマンドライン引数で以下のオプションを付与してください  
--spring.profiles.active=development  
※ 実行環境ごとに設定ファイルを切り替えるため

* -----------2024/01/12 追記-----------
* メール送信機能使う場合はapplication-development.propertiesに
* spring.mail.host=smtp.gmail.com
  spring.mail.port=587
  spring.mail.username=ponponda0103@gmail.com
  spring.mail.password=uolt lsgs vsxq frlp
  spring.mail.properties.mail.smtp.auth=true
  spring.mail.properties.mail.smtp.starttls.enable=true
* 
* 上の記述を入れて起動してください(入れないとerrorが出て起動しないです)
* (上記の設定をいれない)またはメール機能不要な場合はservice/MailServiceクラスを全てコメントアウトしたのち、
* MemberControllerのpublic String sendMail()関数をコメントアウトor削除してください
* またapplication-development.propertiesに設定しているメールアドレスを元にメールを送るようになっていますが、
* これは僕の個人メアドなので別のメアドに変えてください

