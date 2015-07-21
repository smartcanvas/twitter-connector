smartcanvas-twitter-connector
=============================

Twitter Connector for Smart Canvas


#### Running within a docker container

```bash

# Create your Smart Canvas App and fill the variables belos
export SMARTCANVAS_API_CLIENT_ID=FILL_ME_IN
export SMARTCANVAS_API_CLIENT_SECRET=FILL_ME_IN


# Create a twitter app and fill in the variables below
export TWITTER_CONSUMER_KEY=FILL_ME_IN
export TWITTER_CONSUMER_SECRET=FILL_ME_IN
export TWITTER_ACCESS_TOKEN=FILL_ME_IN
export TWITTER_ACCESS_TOKEN_SECRET=FILL_ME_IN


docker run -it --rm -e TWITTER_CONSUMER_KEY=$TWITTER_CONSUMER_KEY -e TWITTER_CONSUMER_SECRET=$TWITTER_CONSUMER_SECRET -e TWITTER_ACCESS_TOKEN=$TWITTER_ACCESS_TOKEN -e TWITTER_ACCESS_TOKEN_SECRET=$TWITTER_ACCESS_TOKEN_SECRET -e SMARTCANVAS_API_CLIENT_ID=$SMARTCANVAS_API_CLIENT_ID -e SMARTCANVAS_API_CLIENT_SECRET=$SMARTCANVAS_API_CLIENT_SECRET smartcanvas/twitter-connector

```
