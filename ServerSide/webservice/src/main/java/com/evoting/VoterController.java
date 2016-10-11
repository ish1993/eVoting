package com.evoting;

import com.evoting.domain.Address;
import com.evoting.domain.Person;
import com.evoting.domain.PoliticalParty;
import com.evoting.repositories.AddressRepository;
import com.evoting.repositories.PersonRepository;
import com.evoting.repositories.PoliticalPartyRepository;
import com.evoting.repositories.UserTypeRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import voter.VoteRequest;
import voter.VoterService;

import javax.json.Json;
import javax.json.JsonObject;


/**
 * Created by Gift on 21/08/16.
 */

@RestController
public class VoterController {

    @Autowired
    PersonRepository pr;

    @Autowired
    UserTypeRepository userType;

    @Autowired
    DatabaseService dbService;

    @Autowired
    PoliticalPartyRepository ppr;

    @Autowired
    AddressRepository ar;

    @CrossOrigin
    @RequestMapping(value = "getParty" , method = RequestMethod.POST)
    public Boolean getParty()
    {

        return false;
    }

    @CrossOrigin
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Boolean register(@RequestBody VoterService newVoter)
    {
        Person newPerson = new Person();
        newPerson.setName(newVoter.getName());
        newPerson.setSurname(newVoter.getSurname());
        newPerson.setCellphone(newVoter.getCellphone());
        newPerson.setEmail(newVoter.getEmail());
        newPerson.setPassword(newVoter.getPassword());
        newPerson.setUserType(userType.findById(1));
        newPerson.setIdNum(newVoter.getIdNum());
        newPerson.setLocationRegistered(newVoter.getLocationRegistered());
        newPerson.setActive(false);
        newPerson.setVotedNationalElection(false);
        newPerson.setVotedProvincialElection(false);

        System.out.println("Trying to persist new Voter");
        pr.saveAndFlush(newPerson);
       // pr.save(newPerson);
        System.out.println("Successful save");

        return true;
    }

    @CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST , produces = "application/JSON")
    public String login(@RequestBody VoterService voterLogin)
    {

        System.out.println(voterLogin.getIdNum());
        System.out.println(voterLogin.getPassword());

        System.out.println("Logging in user");

        Person aPerson = new Person();
        aPerson.setIdNum(voterLogin.getIdNum());
        aPerson.setPassword(voterLogin.getPassword());

       // System.out.println(aPerson.toString());
        Person loggedInAs;
        boolean successful = dbService.validateUser(aPerson);
        if(successful == true)
        {
            loggedInAs  = pr.getPersonByIdNumAndPassword(aPerson.getIdNum(),aPerson.getPassword());
            JsonObject result = Json.createObjectBuilder()
                    .add("success", successful)
                    .add("name", loggedInAs.getName() )
                    .add("surname", loggedInAs.getSurname())
                    .add("IDNum", loggedInAs.getIdNum())
                    .add("votes",loggedInAs.getVotes())
                    .add("votedNational", loggedInAs.isVotedNationalElection())
                    .add("votedProvincial", loggedInAs.isVotedProvincialElection())
                    .add("email", loggedInAs.getEmail())
                    .add("activated", loggedInAs.isActive())
                    .add("locationRegistered", loggedInAs.getLocationRegistered())
                    .build();

            return result.toString();
        }
        else {

             JsonObject result = Json.createObjectBuilder()
                    .add("success",successful)
                    .add("reason" , "Invalid User")
                    .build();
            return result.toString();
        }


        //return new ResponseEntity<>(loggedInAs, HttpStatus.OK);
        //return  successful;

    }

    @CrossOrigin
    @RequestMapping(value = "/castVote", method = RequestMethod.POST)
    public Boolean castVote(@RequestBody VoteRequest voteRequest)
    {
        System.out.println("Cast Vote Request");

        System.out.println(voteRequest.getPartyName());

        PoliticalParty party = ppr.findByPartyName(voteRequest.getPartyName());

        Address votingNode = ar.findByNodeName("Pretoria");




        BlockchainMock blockchain = new BlockchainMock("196.248.196.124","7419", "multichainrpc","51i1XY2ELS96V7xGEA3cGh5iy8KDTxpo2ckaXZ7CBM43");
        JSONObject result = blockchain.sendVoteToNode("15DmYUc17VEx7zvJoAxcPu1fBAREGYVj4ScVwe",1000);

       // BlockchainMock blockchain = new BlockchainMock(votingNode.getIpAddress(),"7419", votingNode.getRpcUsername(),votingNode.getRpcPassword());
       // JSONObject result = blockchain.sendVoteToNode(party.getBlockchainNodeAddress(),1000);

        if(result.get("success").toString().equals("true"))
        return true;
        else
            return false;
    }


}