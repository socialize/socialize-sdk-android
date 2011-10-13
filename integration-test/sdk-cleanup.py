import oauth2 as oauth
import simplejson
import random as rand
import os,sys

config_file_path='../sample/assets/sample-app.conf'
assets_file_path='../sample/assets/existing-data/'

def create_android_config(key,secret,url,fb_app_id):
    print '#'*20
    print '## CREATE conf.js ##'
    print '#'*20           
    
    f = open(config_file_path,'w')
    text = 'socialize.consumer.key='+key
    text+= '\nsocialize.consumer.secret='+secret
    text+= '\napi.host='+url
    text+= '\nfacebook.app.id='+fb_app_id
    text+= '\n'
    f.write(text)
    print text
    f.close()

def read_android_config():
    file = open(config_file_path,'r')
#file = open('../sample/assets/sample-app.conf','r')

    for line in file :
        li=line.strip()
        if not li.startswith("#"):
            if li.startswith('socialize.consumer.key'):
                key = li[li.find('=')+1:]
            elif li.startswith('socialize.consumer.secret'):
                secret = li[li.find('=')+1:]
            elif li.startswith('api.host'):
                url =  li[li.find('=')+1:]
                if not url.startswith('http'):    ## if not start with http
                    url = 'http://'+url           ## using http:// protocal
            #print line.rstrip()
    return key,secret,url     

def gen_comment(entity_key,i):
    text = 'POST comment #%i'%(i)
    return {'entity_key': entity_key, 'text': text, 'lat':rand.random()*90, 'lng':rand.random()*90}

def gen_like_and_view(entity_key):
    return {'entity_key': entity_key, 'lat':rand.random()*90, 'lng':rand.random()*90}

def gen_share(entity_key, medium):
    share_dic={1:'Twitter',
                2:'Facebook',
                3:'Email',
                4:'Other',             
            }
    text = 'POST SHARE on %s'%(share_dic[medium])
    
    return {'entity_key': entity_key,'text':text, 'medium':medium, 'lat':rand.random()*90, 'lng':rand.random()*90}     

def print_json(item, fname=None):
    item = simplejson.loads(item)
    if not fname:
            print simplejson.dumps(item,sort_keys=True, indent=4)

    else:
        fname = assets_file_path+fname
        print '\t**generate outfile:',fname
        if not os.path.exists(assets_file_path):
            
            os.makedirs(dir)
        
        f = open(fname, 'w')
        f.write(simplejson.dumps(item,sort_keys=True, indent=4))
        f.close()                                          

def remove(fname):
    try:
        fname = assets_file_path+fname
        os.remove(fname)
        print '\t %s DELETED'%fname
    except OSError:
        print '\tfile not found', fname  

def make_request(client, req_url, method, data, outfile=None):
    payload = simplejson.dumps(data)
    resp=client.request(uri=req_url, method=method, body='payload='+payload)
    if resp[0]['status']!='200':
        print 'ERROR:',resp[0]['status']
        print resp[1]
        sys.exit(2)
    
    try:
        cont = simplejson.loads(resp[1])
        print_json(resp[1],outfile)    
    except ValueError:
        print resp[1]      

def main(key,secret,url):
    sig = oauth.SignatureMethod_HMAC_SHA1()    
    auth_url = 'authenticate/'
    purge_url = 'all_data/'
    entity_url = 'entity/'
    comment_url= 'comment/'
    share_url='share/'
    like_url='like/'
    view_url='view/'
    udid = '1234566788'

    auth_url = url+auth_url
    print auth_url
    
    print '#'*20
    print '## CLEANUP SCRIPT START ##'
    print '#'*20
    
## AUTHENTICATION ##
    consumer = oauth.Consumer( key, secret)
    client = oauth.Client( consumer) 
    payload = simplejson.dumps({ 'payload': { 'udid': udid}})

    auth_resp = client.request(auth_url ,'POST', body='payload='+simplejson.dumps({'udid':udid}))
    auth_cont = simplejson.loads(auth_resp[1])
    oauth_secret= auth_cont['oauth_token_secret']
    oauth_token= auth_cont['oauth_token']
    token = oauth.Token(oauth_token,oauth_secret) 

## Create client with oauth token
    client=oauth.Client(consumer,token)
    print '#'*20
    print '## PURGE ALL DATA'
    print '#'*20    

## PURGE ALL DATA ##

    req_url = url + purge_url
    make_request(client, req_url,method='DELETE', data={}, outfile=None)

    print '#'*20
    print '## REMOVE json files ##'
    print '#'*20  
     
    remove('comments.json')
    remove('shares.json')
    remove('likes.json')
    remove('views.json')


## Create Entity
    print '#'*20
    print '## CREATE ENTITY ##'
    print '#'*20

    entities = [ {'key':'http://entity1.com','name':'First Entity'},
                 {'key':'http://entity2.com','name':'Second Entity'}
                ]
    req_url = url+entity_url
    make_request(client, req_url,method='POST', data=entities, outfile='entities.json')

    print '#'*20
    print '## CREATE COMMENT ##'
    print '#'*20
                
    comments = [ gen_comment('http://entity1.com', i) for i in range(10)]
    req_url = url+comment_url
    make_request(client, req_url,method='POST', data=comments, outfile='comments.json')

    print '#'*20
    print '## CREATE LIKES ##'
    print '#'*20  

    likes = [ gen_like_and_view('http://entity1.com') , gen_like_and_view('http://entity2.com')]
    req_url = url+like_url 
    make_request(client, req_url,method='POST', data=likes, outfile='likes.json')

## Create Share for both entities

    print '#'*20
    print '## CREATE SHARES ##'
    print '#'*20  

    shares = [ gen_share('http://entity1.com',1) , gen_share('http://entity2.com',2)]
    req_url = url+share_url 
    make_request(client, req_url,method='POST', data=shares, outfile='shares.json')


    print '#'*20
    print '## CREATE VIEWS ##'
    print '#'*20  

    views = [ gen_like_and_view('http://entity1.com') , gen_like_and_view('http://entity2.com')]
    req_url = url+view_url 
    make_request(client, req_url,method='POST', data=views, outfile='views.json')    

if __name__ == "__main__":
    args = sys.argv
    if len(args)<4:
        print '\tusage: python sdk-cleanup.py <consumer-key> <consumer-secret> <http://api.socialize.com/v1> [facebook_app_id]'
        sys.exit(2)
    elif not args[3].startswith('http://'):
        print '\tinvalid format for <http://api.socialize.com/v1>'
        sys.exit(2)                              
    key = args[1]
    secret = args[2]
    url = args[3]             
    fb_app_id = '0' # default facebook app id
    if len(args)==5:
        fb_app_id = args[4]
    create_android_config(key,secret,url,fb_app_id)
    key, secret, url = read_android_config()
    if url[-1]!='/':
        url+='/'
    
    if not url.startswith('http://api.getsocialize.com'):
        sys.exit(main(key,secret,url))
    else:
        sys.exit(0)
