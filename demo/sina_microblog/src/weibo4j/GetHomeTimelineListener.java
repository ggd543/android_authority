package weibo4j;

import java.util.List;

public interface GetHomeTimelineListener
{
    public void ok(List<Status> statusList);
    public void error(Exception e);
}
