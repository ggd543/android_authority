package weibo4j;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import weibo4j.http.Response;

public class Count extends WeiboResponse
{
	private long id;
	private int commentCount;
	private int repostCount;

	private Count(Response response, Element element, Weibo twitter)
			throws WeiboException
	{
		super(response);
		init(response, element, twitter);
	}

	private void init(Response response, Element elem, Weibo twitter)
			throws WeiboException
	{

		ensureRootNodeNameIs("count", elem);

		repostCount = getChildInt("rt", elem);
		commentCount = getChildInt("comments", elem);

	}

	static Count constructCount(Response response, Weibo twitter)
			throws WeiboException
	{
		Document doc = response.asDocument();
		ensureRootNodeNameIs("counts", doc);

		NodeList list = doc.getDocumentElement().getElementsByTagName("count");
		if (list.getLength() > 0)
		{
			Element count = (Element) list.item(0);
			return new Count(response, count, twitter);
		}
		else
		{
			return null;
		}
	}

	static List<Count> constructCountList(Response response, Weibo twitter)
			throws WeiboException
	{
		Document doc = response.asDocument();
		ensureRootNodeNameIs("counts", doc);

		NodeList list = doc.getDocumentElement().getElementsByTagName("count");
		List<Count> countList = new ArrayList<Count>();
		for (int i = 0; i < list.getLength(); i++)
		{
			Element count = (Element) list.item(i);
			countList.add(new Count(response, count, twitter));

		}
		return countList;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public int getCommentCount()
	{
		return commentCount;
	}

	public void setCommentCount(int commentCount)
	{
		this.commentCount = commentCount;
	}

	public int getRepostCount()
	{
		return repostCount;
	}

	public void setRepostCount(int repostCount)
	{
		this.repostCount = repostCount;
	}

}
