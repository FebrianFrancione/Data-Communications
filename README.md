# Data-Communications-A1
COMP 445 Assignments - TCP using UdP included
HTTP client, HTTP File server and UDP datagramChannel implementing the various RDT(Reliable Data Transfers 1.0-3.0)

When Using code: 
Go in terminal: enter cd src: enter Javac HTTPclient.java, then Java Httpclient to enter the command line for this assignment.
Follow the command line queries and enter appropriate reponse required.

Some commands and outputs:

GET REQUEST
httpc get 'http://httpbin.org/get?course=networking&assignment=1'
returns:
{
  "args": {
    "assignment": "1",
    "course": "networking"
  },
  "headers": {
    "Host": "a0207c42-pmhttpbin-pmhttpb-c018-592832243.us-east-1.elb.amazonaws.com",
    "X-Amzn-Trace-Id": "Root=1-5f7a72dc-5596caac67321f6b0481dd90"
  },
  "origin": "147.253.131.76",
  "url": "http://a0207c42-pmhttpbin-pmhttpb-c018-592832243.us-east-1.elb.amazonaws.com/get?course=networking&assignment=1"
}

GET with VERBOSE
httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' 
returns:
HTTP/1.1 200 OK 
Server: nginx 
Date: Fri, 1 Sep 2017 14:52:12 GMT 
Content-Type: application/json Content-Length: 255 Connection: close 
Access-Control-Allow-Origin: * 
Access-Control-Allow-Credentials: true 
{ 
"args": { "assignment": "1", 
"course": "networking" 
}, 
"headers": { 
"Host": "httpbin.org", 
"User-Agent": "Concordia-HTTP/1.0" 
}, 
"url": "http://httpbin.org/get?course=networking&assignment=1" 
} 

POST with INLINE DATA
httpc post -h Content-Type:application/json -d '{"Assignment": 1}' http://httpbin.org/post 
returns
{ 
"args": {}, 
"data": "{\"Assignment\": 1}", "files": {}, Comp445 – Fall 2020 - Lab Assignment # 1 Page 7 

"form": {}, "headers": { 
"Content-Length": "17", 
"Content-Type": "application/json", "Host": "httpbin.org", 
"User-Agent": "Concordia-HTTP/1.0" 
}, 
"json": { "Assignment": 1 
}, 
"url": "http://httpbin.org/post" 
} 

Help command
httpc is a curl-like application but supports HTTP protocol only.
Usage:
httpc command [arguments] The commands are:
get executes a HTTP GET request and prints the response. post executes a HTTP POST request and prints the response. help prints this screen.
Use "httpc help [command]" for more information about a command.





