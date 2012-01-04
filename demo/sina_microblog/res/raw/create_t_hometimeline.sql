CREATE TABLE [t_hometimeline] (
  [status_id] INT64 NOT NULL, 
  [name] VARCHAR(100) NOT NULL, 
  [post_time] DATETIME NOT NULL, 
  [text] TEXT, 
  [image_url_hashcode] INT DEFAULT 0, 
  CONSTRAINT [sqlite_autoindex_t_hometimeline_1] PRIMARY KEY ([status_id]));