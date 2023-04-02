package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;//group object->users list
    private HashMap<Group, List<Message>> groupMessageMap;//group object->messages list
    private HashMap<Message, User> senderMap;//message obj->user(sender) object
    private HashMap<Group, User> adminMap;//mapping of admins to groups to group object
    private HashMap<String,User> userData;//user mobile->user
    private HashMap<Integer,Message> messageData;

    private int customGroupCount;//no of groups created count
    private int messageId;//no of messages created

    public boolean isNewUser(String mobile) {
        return !userData.containsKey(mobile);
    }

    public String  createUser(String name, String mobile) {
        userData.put(mobile,new User(name,mobile));
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        String groupname="";

        if(users.size()==2)
            groupname =users.get(1).getName();

        else if(users.size()>2)
            groupname="Group "+customGroupCount++;

        Group g=new Group(groupname,users.size());
        groupUserMap.put(g,users);
        adminMap.put(g,users.get(0));
        return g;
    }

    public int createMessage(String content) {
        messageData.put(messageId,new Message(messageId,content));
        return messageId++;
    }


    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        boolean found=false;
        for(User user:groupUserMap.get(group))
        {

            if(user==sender)
            {
               found=true;
               break;
            }
        }
        if(!found)
            throw new Exception("You are not allowed to send message");
        else {
            senderMap.put(message,sender);
           if(groupMessageMap.containsKey(group))
           {
               List<Message> list=groupMessageMap.get(group);
               list.add(message);
               groupMessageMap.put(group,list);
           }
           else {
               List<Message> list=new ArrayList<>();
               list.add(message);
               groupMessageMap.put(group,list);
           }
        }
        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS".
        if(!adminMap.containsKey(group))
        {
            throw new Exception("Group does not exist");
        }
        else if(adminMap.get(group)!=approver)
        {
            throw new Exception("Approver does not have rights");
        }
        boolean found=false;
        for(User u:groupUserMap.get(group))
        {

            if(u==user)
            {
                found=true;
                break;
            }
        }
        if(!found)
            throw new Exception("User is not a participant");
        else {
            adminMap.put(group,user);
        }
        return "SUCCESS";

    }

    public int removeUser(User user) throws Exception {
        boolean found=false;int ans=0;
        for(Group group: groupUserMap.keySet())
        {
            for(User u:groupUserMap.get(group))
            {
                if(u==user)
                {
                    found=true;
                    if(adminMap.get(group)==u)
                        throw new Exception("Cannot remove admin");
                    else {
                        groupUserMap.get(group).remove(u);
                        for(Message m:senderMap.keySet())
                        {
                            if(senderMap.get(m)==u) {
                                senderMap.remove(m);
                                messageData.remove(m.getId());
                                groupMessageMap.get(group).remove(m);
                            }
                        }
                        ans+= groupMessageMap.get(group).size()+messageData.size()+groupUserMap.get(group).size();
                    }
                }
            }
            if(found)break;
        }
        if(!found)
            throw new Exception("User not found");
        return ans;
    }



    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userData = new HashMap<>();
        this.customGroupCount = 1;
        this.messageId = 1;
    }

    public HashMap<Group, List<User>> getGroupUserMap() {
        return groupUserMap;
    }

    public void setGroupUserMap(HashMap<Group, List<User>> groupUserMap) {
        this.groupUserMap = groupUserMap;
    }

    public HashMap<Group, List<Message>> getGroupMessageMap() {
        return groupMessageMap;
    }

    public void setGroupMessageMap(HashMap<Group, List<Message>> groupMessageMap) {
        this.groupMessageMap = groupMessageMap;
    }

    public HashMap<Message, User> getSenderMap() {
        return senderMap;
    }

    public void setSenderMap(HashMap<Message, User> senderMap) {
        this.senderMap = senderMap;
    }

    public HashMap<Group, User> getAdminMap() {
        return adminMap;
    }

    public void setAdminMap(HashMap<Group, User> adminMap) {
        this.adminMap = adminMap;
    }


    public int getCustomGroupCount() {
        return customGroupCount;
    }

    public void setCustomGroupCount(int customGroupCount) {
        this.customGroupCount = customGroupCount;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public HashMap<String, User> getUserData() {
        return userData;
    }

    public void setUserData(HashMap<String, User> userData) {
        this.userData = userData;
    }

    public HashMap<Integer, Message> getMessageData() {
        return messageData;
    }

    public void setMessageData(HashMap<Integer, Message> messageData) {
        this.messageData = messageData;
    }


    public String findMessage(Date start, Date end, int k) throws Exception {
        List<Message> list=new ArrayList<>();
        for(Message m:messageData.values())
        {
            if(m.getTimestamp().compareTo(start)>0&&m.getTimestamp().compareTo(end)<0)
                list.add(m);
        }
        String message="";
        if(list.size()<k)
            throw new Exception("K is greater than the number of messages");
        else {
            Collections.sort(list, new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }
            });
            message=list.get(list.size()-k-1).getContent();
        }
        return message;
    }

}
