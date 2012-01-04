package weibo4j;

public interface OnUpdateCommentListener
{
    public void onSuccess(Comment comment);
    public void onException(Exception e);
}
