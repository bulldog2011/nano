package com.javacodegeeks.android.apps.moviesearchapp.model;

import java.io.Serializable;

import com.leansoft.nano.annotation.Default;
import com.leansoft.nano.annotation.Element;

@Default
public class Movie implements Serializable {
	
	private static final long serialVersionUID = 5114870955079482108L;

	public String score;
	public String popularity;
	public boolean translated;
	public boolean adult;
	public String language;
	@Element(name="original_name")
	public String originalName;
	public String name;
	public String type;
	public String id;
	@Element(name="imdb_id")
	public String imdbId;
	public String url;
	public String votes;
	public String rating;
	public String certification;
	public String overview;
	public String released;
	public String version;
	@Element(name="last_modified_at")
	public String lastModifiedAt;
	
	@Element(name="images")
	public ImageContainer imageContainer;
	
	public String retrieveThumbnail() {
		if (imageContainer!=null && imageContainer.imageList != null && !imageContainer.imageList.isEmpty()) {
			for (Image movieImage : imageContainer.imageList) {
				if (movieImage.size.equalsIgnoreCase(Image.SIZE_THUMB) &&
						movieImage.type.equalsIgnoreCase(Image.TYPE_POSTER)) {
					return movieImage.url;
				}
			}
		}
		return null;
	}
	
	public String retrieveCoverImage() {
		if (imageContainer!=null && imageContainer.imageList != null && !imageContainer.imageList.isEmpty()) {
			for (Image movieImage : imageContainer.imageList) {
				if (movieImage.size.equalsIgnoreCase(Image.SIZE_COVER) &&
						movieImage.type.equalsIgnoreCase(Image.TYPE_POSTER)) {
					return movieImage.url;
				}
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Movie [name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}	

}
