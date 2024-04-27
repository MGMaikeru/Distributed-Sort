module Caller
{
    interface Receiver
    {
        void receiveMessage(string msg);
        void startSubServer(int from, int to,string filename,string basepath);
        string getListSorted();
    }
    interface Sender
    {
        void sendMessage(Receiver* proxy,string msg);
        void shutdown();
    }
}