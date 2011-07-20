import oauth2 as oauth
import simplejson

sig = oauth.SignatureMethod_HMAC_SHA1()    
auth_url = '/authenticate/'
purge_url = '/all_data/'   
udid = '1234566788'
file = open('../sample/assets/sample-app.conf','r')
for line in file :
    li=line.strip()
    if not li.startswith("#"):
        if li.startswith('socialize.consumer.key'):
            key = li[li.find('=')+1:]
        elif li.startswith('socialize.consumer.secret'):
            secret = li[li.find('=')+1:]
        elif li.startswith('socialize.api.url'):
            url =  li[li.find('=')+1:]
            if not url.startswith('http'):    ## if not start with http
                url = 'http://'+url           ## using http:// protocal
        #print line.rstrip()
print 'KEY=',key
print 'SECRET=',secret
print 'URL=',url
auth_url = url+auth_url
print 'AUTH_URL=',auth_url
## AUTHENTICATION ##
consumer = oauth.Consumer( key, secret)
client = oauth.Client( consumer) 
payload = simplejson.dumps({ 'payload': { 'udid': udid}})
auth_resp = client.request(auth_url ,'POST', body='payload='+simplejson.dumps({'udid':udid}))
auth_cont = simplejson.loads(auth_resp[1])
oauth_secret= auth_cont['oauth_token_secret']
oauth_token= auth_cont['oauth_token']
token = oauth.Token(oauth_token,oauth_secret) 

## PURGE ALL DATA ##
req_url = url + purge_url
req = oauth.Request.from_consumer_and_token( consumer,
                                            http_method='POST',
                                            parameters = {},
                                            http_url = req_url,
                                            token = token)
req.sign_request(sig, consumer, token)
resp = client.request(uri=req_url ,method='POST', headers=req.to_header())
print resp[1]
