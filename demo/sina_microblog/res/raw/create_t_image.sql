CREATE TABLE [t_image] (
  [image_url_hashcode] INT, 
  [image] BINARY, 
  [image_type] INT NOT NULL, 
  CONSTRAINT [sqlite_autoindex_t_photo_1] PRIMARY KEY ([image_url_hashcode]));