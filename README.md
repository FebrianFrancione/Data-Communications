# Data-Communications-A1
COMP 445 A1

When Using code: 
Go in terminal: enter cd src: enter Javac HTTPclient.java, then Java Httpclient to enter the command line for this assignment.
Follow the command line queries and enter appropriate reponse required.

Some commands and outputs:
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


