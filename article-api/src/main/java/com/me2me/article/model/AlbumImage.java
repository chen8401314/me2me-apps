package com.me2me.article.model;

public class AlbumImage {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column album_image.id
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    private Long id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column album_image.album_id
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    private Long albumId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column album_image.image_url
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    private String imageUrl;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column album_image.id
     *
     * @return the value of album_image.id
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column album_image.id
     *
     * @param id the value for album_image.id
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column album_image.album_id
     *
     * @return the value of album_image.album_id
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    public Long getAlbumId() {
        return albumId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column album_image.album_id
     *
     * @param albumId the value for album_image.album_id
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    public void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column album_image.image_url
     *
     * @return the value of album_image.image_url
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column album_image.image_url
     *
     * @param imageUrl the value for album_image.image_url
     *
     * @mbggenerated Fri May 27 14:31:45 CST 2016
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }
}