package com.liferay.custom.expire;

import com.liferay.mail.kernel.model.MailMessage;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.petra.content.ContentUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import org.osgi.service.component.annotations.Reference;

import javax.mail.internet.InternetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ExpiredUsers {

    public void update(){
        _log.info("notify: ");
        List<User> users = _UserLocalService.getUsers(0,9999);
        for (User user:users)
        {
            Date lastLogin = user.getLastLoginDate();
            if ((lastLogin.compareTo(Calendar.getInstance().getTime()) == -30) ||
               (lastLogin.compareTo(Calendar.getInstance().getTime()) == -40) ||
               (lastLogin.compareTo(Calendar.getInstance().getTime()) == -50)) {
                //todo find users not logged in for 30 days - send email
                //todo find users not logged in for 40 days - send email
                //todo find users not logged in for 50 days - send email
                sendMail("Your account will be deativated. ACTION REQUIRED!",user.getDisplayEmailAddress());
            }
            if ((lastLogin.compareTo(Calendar.getInstance().getTime()) == -70) ||
               (lastLogin.compareTo(Calendar.getInstance().getTime()) == -80)) {
                //todo find users not logged in for 70 days - send email
                //todo find users not logged in for 80 days - send email
                sendMail("Your account will be removed. ACTION REQUIRED!",user.getDisplayEmailAddress());
            }
        }
    }

    public void deactivate(){
        _log.info("deactivate: " );

        //todo find users not logged in for 60 days - deactivate, send email,NEVER ADMIN!

    }

    public void remove(){
        _log.info("remove: ");

        //todo find users not logged in for 90 days - remove, send email
    }

    private void sendMail(String subject, String toAddress){
        //body
        Class<?> clazz = getClass();
        String body = ContentUtil.get(clazz.getClassLoader(),"META-INF/resources/template/email.htm");
        //mailtemplate = mailtemplate.replaceAll("\\$\\{DOWNLOAD_URL\\}",downloadUrl.toString());
        //mailtemplate = mailtemplate.replaceAll("\\$\\{MESSAGE\\}",uploadPortletRequest.getParameter("message"));
        //mailtemplate = mailtemplate.replaceAll("\\$\\{PASSWORD\\}",uploadPortletRequest.getParameter("password"));
        //StringBuilder body = new StringBuilder(mailtemplate);

        // from
        InternetAddress from = new InternetAddress();
        from.setAddress("admin@liferay.com");

        // to
        InternetAddress to = new InternetAddress();
        to.setAddress(toAddress);

        MailMessage mailMessage = new MailMessage(from,to,subject,body,Boolean.TRUE);
        _mailService.sendEmail(mailMessage);

        System.out.println("Mail send");
    }

    @Reference
    protected UserLocalService _UserLocalService;

    @Reference
    private MailService _mailService;

    private static final Log _log = LogFactoryUtil.getLog(ExpiredUsers.class);
}
