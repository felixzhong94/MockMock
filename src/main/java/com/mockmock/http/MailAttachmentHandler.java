package com.mockmock.http;

import com.mockmock.mail.MailQueue;
import com.mockmock.mail.MockMail;
import com.mockmock.mail.MockMail.Attachment;

import org.eclipse.jetty.server.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeUtility;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pengzili on 2016/12/30.
 */

@Service
public class MailAttachmentHandler extends BaseHandler{

    private String pattern = "^/attachment/([0-9]+)/([0-9]+)/?$";

    private MailQueue mailQueue;

    @Override
    public void handle(String target, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
        if(!isMatch(target))
        {
            return;
        }

        long mailId = getMailId(target);
        if(mailId == 0)
        {
            return;
        }
        
        long attachmentId = getAttachmentId(target);
        if(attachmentId == 0)
        {
            return;
        }

        MockMail mockMail = this.mailQueue.getById(mailId);

        if(mockMail == null || !mockMail.hasAttachment())
        {
            return;
        }
        
        //mail can have empty html bodies
        //if(mockMail.getBodyHtml() == null)
        //{
        //    return;
        //}


        List<Attachment> attachments = mockMail.getAttachments();
        int index = (int)(attachmentId - 1);
        
        if(attachmentId > Integer.MAX_VALUE || index < 0 || index > attachments.size())
        {
        	return;
        }
        
        Attachment attachment = attachments.get(index);
        
        if(attachment == null || attachment.getContentType() == null || attachment.getFileName() == null || attachment.getData() == null)
        {
        	return;
        }
        
        response.setContentType(attachment.getContentType());
        response.setHeader("Content-Disposition", " attachment;filename="+ MimeUtility.encodeWord(attachment.getFileName()));
        response.getOutputStream().write(attachment.getData());
        response.setStatus(HttpServletResponse.SC_OK);
        
        request.setHandled(true);
    }

    /**
     * Checks if this handler should be used for the given target
     * @param target String
     * @return boolean
     */
    private boolean isMatch(String target)
    {
        return target.matches(pattern);
    }

    /**
     * Returns the mail id if it is part of the target
     * @param target String
     * @return long
     */
    private long getMailId(String target)
    {
        Pattern compiledPattern = Pattern.compile(pattern);

        Matcher matcher = compiledPattern.matcher(target);
        if(matcher.find())
        {
            String result = matcher.group(1);
            try
            {
                return Long.valueOf(result);
            }
            catch (NumberFormatException e)
            {
                return 0;
            }
        }

        return 0;
    }
    
    private long getAttachmentId(String target)
    {
        Pattern compiledPattern = Pattern.compile(pattern);

        Matcher matcher = compiledPattern.matcher(target);
        if(matcher.find())
        {
            String result = matcher.group(2);
            try
            {
                return Long.valueOf(result);
            }
            catch (NumberFormatException e)
            {
                return 0;
            }
        }

        return 0;
    }

    @Autowired
    public void setMailQueue(MailQueue mailQueue) {
        this.mailQueue = mailQueue;
    }
}
