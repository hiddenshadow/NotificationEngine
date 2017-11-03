package services;

public interface MailService {
    public boolean sendMail();
}

class MailServiceImpl implements MailService {

    @Override
    public boolean sendMail() {

        return false;
    }
}