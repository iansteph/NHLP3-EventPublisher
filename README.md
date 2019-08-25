![AWS CodeBuild build badge](https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoic0dIUlRhanpYOHVDd3BCOTRIZ0szdG10K09taUY2eDFNTGxiM3FFdTIwaWh3U3NDZTZ6dEM5TlB3MWllbVNsSVhOYU1jRGRIak5vVDZjamJRVkVTWkNjPSIsIml2UGFyYW1ldGVyU3BlYyI6Ikppd2xVTWpJcmU3V2laY0ciLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)

# NHLP3
Serverless **N**HL **P**lay-by-**P**lay **P**rocessor *(hence NHLP3)* built using [AWS Lambda](https://aws.amazon.com/) and NHL's public APIs *(more information can be found at [Drew Hynes's](https://gitlab.com/dword4) [NHL Stats API Documentation project](https://gitlab.com/dword4/nhlapi/blob/master/stats-api.md))*.

![NHLP3 Diagram](https://pbs.twimg.com/media/ECWDcoCUEAAUnoC?format=png)

# EventPublisher Function
The purpose of this function is to be called once daily and retrieve all of the NHL games for the specified day. It will
then set up the play-by-play processor to process each game at the start of each game.
