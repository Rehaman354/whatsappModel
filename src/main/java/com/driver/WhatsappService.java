package com.driver;

import java.util.Date;
import java.util.List;

public class WhatsappService {

    WhatsappRepository whatsappRepo=new WhatsappRepository();
    public String createUser(String name, String mobile) throws Exception {
        if(whatsappRepo.isNewUser(mobile)==false)
            throw new Exception("User already exists");
      return  whatsappRepo.createUser(name,mobile);
    }

    public Group createGroup(List<User> users) {
        return whatsappRepo.createGroup(users);
    }

    public int createMessage(String content) {
        return whatsappRepo.createMessage(content);
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(!whatsappRepo.getGroupUserMap().containsKey(group))
            throw new Exception("Group does not exist");
        return whatsappRepo.sendMessage(message,sender,group);
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        return whatsappRepo.changeAdmin(approver,user,group);
    }

    public String findMessage(Date start, Date end, int k)  throws Exception{
        return whatsappRepo.findMessage(start,end,k);
    }

    public int removeUser(User user) throws Exception {
        return whatsappRepo.removeUser(user);
    }
}
