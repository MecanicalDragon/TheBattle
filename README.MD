### Hi there!
Here I develop online game on enterprise technologies, following 2 purposes:

    - To gain more experience in relevant technologies
    - To do something, I like)
    
### If you want to try it
Just go [here](https://thebattle.herokuapp.com/) and play with your friend.

### If you want to build it locally
You need java, gradle, node.js and yarn. Input into console following commands:

    - gradle buildLocally
    - cp ./build/libs/TheBattle-0.98.6.jar ./TheBattle-0.98.6.jar
    - java -jar TheBattle-0.98.6.jar
    
If you use Linux there might be some problems with path variables definitions or 
sudo permissions. If so, you can download compiled jar [here](https://yadi.sk/d/jTv1cikc1CclBw)
    
The game will start on http://localhost:9191 in your browser. 
Btw, you need another browser and another player if you want to try it, cause there is no AI for a while)
Also, if you want to connect from remote, before these steps change 'DEPLOYED_URL' in webpack.config.js to your actual ip address.