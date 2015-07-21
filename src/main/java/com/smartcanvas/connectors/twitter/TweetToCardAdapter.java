package com.smartcanvas.connectors.twitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.joda.time.DateTime;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.URLEntity;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Sets;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.smartcanvas.Smartcanvas;
import com.smartcanvas.model.Attachment;
import com.smartcanvas.model.Author;
import com.smartcanvas.model.Card;
import com.smartcanvas.model.ContentProvider;
import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.message.StallWarningMessage;

final class TweetToCardAdapter implements StatusStreamHandler {
    
    private Configuration cfg;
    private Smartcanvas smartcanvas;

    public TweetToCardAdapter(Configuration cfg) throws Throwable {
        super();
        this.cfg = cfg;
        this.smartcanvas = new Smartcanvas(
                new NetHttpTransport(),
                new JacksonFactory(),
                    cfg.getString("SMARTCANVAS_API_CLIENT_ID"), 
                    cfg.getString("SMARTCANVAS_API_CLIENT_SECRET")
                );
    }

    @Override
    public void onStatus(Status status) {

        if (cfg.getBoolean("smartcanvas_connectors_twitter_onlyPhotos", true) && !containAnyPhoto(status.getMediaEntities()) ) {
            return;
        }
        
        if (!status.isRetweet()) {

            ContentProvider provider = provider(status);
            Author author = author(status);
            Card card = new Card(provider);
            card.setAuthor(author);
            card.setContent(status.getText());
            card.setTitle(title(status));
            card.setSummary(title(status));
            card.setCategories(hashtags(status.getHashtagEntities()));
            card.setCreateDate(new DateTime(status.getCreatedAt()));
            card.setAutoApprove(true);

//            if (status.getGeoLocation() != null) {
//                card.setGeoCode(String.format("%s %s", status.getGeoLocation().getLatitude(), status
//                        .getGeoLocation().getLongitude()));
//            }

//            card.addLanguage(status.getLang());
//            card.addLanguage("pt");
//            card.addRegion("br");

//            if (status.getPlace() != null) {
//                card.setPlaceName(status.getPlace().getName());
//                card.setAddress(status.getPlace().getStreetAddress());
//                card.addRegion(status.getPlace().getCountryCode());
//            }
            

            if (containAnyPhoto(status.getMediaEntities())) {
                for (final MediaEntity mediaEntity : status.getMediaEntities()) {
                    Attachment photo = Attachment.photo(mediaEntity.getMediaURL() + ":large");
                    photo.setContentURL(mediaEntity.getExpandedURL());
                    card.addAttachment(photo);
                }
            } else {
                for (final URLEntity urlEntity : status.getURLEntities()) {
                    Attachment attachment = Attachment.article();
                    attachment.setDisplayName(urlEntity.getDisplayURL());
                    attachment.setContentURL(urlEntity.getExpandedURL());
                    card.addAttachment(attachment);
                }
            }

            try {
                smartcanvas.addCard(card);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private Author author(Status status) {
        Author author = new Author();
        author.setDisplayName(status.getUser().getScreenName());
        author.setId(String.valueOf(status.getUser().getId()));
        author.setImageURL(status.getUser().getOriginalProfileImageURL());
        return author;
    }

    private ContentProvider provider(Status status) {
        ContentProvider provider = new ContentProvider("twitter",
                Long.toString(status.getId()), Long.toString(status.getUser().getId()));
        String contentURL = "https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId();
        provider.setContentURL(contentURL);
//            provider.setCreateDate(status.getCreatedAt());
//            provider.setUpdateDate(status.getCreatedAt());
//            provider.setPublishedDate(status.getCreatedAt());
        return provider;
    }

    private String title(Status status) {
        URLEntity[] urlEntities = status.getURLEntities();
        String title = status.getText();
        if (urlEntities.length > 0) {
            for (int i = 0; i < urlEntities.length; i++) {
               String text = status.getText();
               text.replaceFirst(urlEntities[i].getURL(), "");
            }
        } 
        return title;
    }

    private Set<String> hashtags(HashtagEntity[] hashtagEntities) {
        Set<String> hashtags = Sets.newHashSet();
        for (HashtagEntity hashtagEntity : hashtagEntities) {
            hashtags.add(hashtagEntity.getText());
        }
        return hashtags;
    }

    private boolean containAnyPhoto(final MediaEntity[] medias) {
        return FluentIterable.from(Arrays.asList(medias)).anyMatch(new Predicate<MediaEntity>() {
            @Override
            public boolean apply(final MediaEntity input) {
                return "photo".equals(input.getType());
            }
        });
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onTrackLimitationNotice(int limit) {
    }

    @Override
    public void onScrubGeo(long user, long upToStatus) {
    }

    @Override   
    public void onStallWarning(StallWarning warning) {
    }

    @Override
    public void onException(Exception e) {
    }

    @Override
    public void onDisconnectMessage(DisconnectMessage message) {
    }

    @Override
    public void onStallWarningMessage(StallWarningMessage warning) {
    }

    @Override
    public void onUnknownMessageType(String s) {
    }
}