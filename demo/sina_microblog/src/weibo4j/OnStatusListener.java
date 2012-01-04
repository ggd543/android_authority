package weibo4j;

public interface OnStatusListener
{
    public void onSuccess(Status status);
    public void onException(Exception e);
}
