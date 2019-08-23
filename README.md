# NHLP3
Serverless **N**HL **P**lay-by-**P**lay **P**rocessor *(hence NHLP3)* built using [AWS Lambda](https://aws.amazon.com/) and NHL's public APIs *(more information can be found at [Drew Hynes's](https://gitlab.com/dword4) [NHL Stats API Documentation project](https://gitlab.com/dword4/nhlapi/blob/master/stats-api.md))*.

![NHLP3 Diagram](https://pbs.twimg.com/media/ECWDcoCUEAAUnoC?format=png)

# EventPublisher Function
The purpose of this function is to be called once daily and retrieve all of the NHL games for the specified day. It will
then set up the play-by-play processor to process each game at the start of each game.
