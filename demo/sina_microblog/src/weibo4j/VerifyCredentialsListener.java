package weibo4j;

public interface VerifyCredentialsListener
{
    public void ok(User user);
    public void error(Exception e);
}
