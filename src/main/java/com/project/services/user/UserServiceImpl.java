package com.project.services.user;

import com.project.converters.ModelToDto;
import com.project.dtos.UserDto;
import com.project.models.Competition;
import com.project.models.User;
import com.project.repositories.CompetitionRepository;
import com.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final CompetitionRepository competitionRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           CompetitionRepository competitionRepository) {
        this.userRepository = userRepository;
        this.competitionRepository = competitionRepository;
    }

    @Override
    public UserDto getUserByCnp(String cnp) {
        return ModelToDto.userToDto(getByCnp(cnp));
    }

    private User getByCnp(String cnp) {
        return userRepository.findByCnp(cnp)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(ModelToDto::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public String createNewUser(UserDto userDto) {
        User user = User.builder()
                .cnp(userDto.getCnp())
                .name(userDto.getName())
                .age(userDto.getAge())
                .gender(userDto.getGender())
                .participatesToCompetitions(participatesToCompetitions(userDto.getCompetitionsIds()))
                .build();

        updateCompetitionsFundraisingBudget(userDto.getCompetitionsIds());

        return userRepository.save(user).getCnp();
    }

    private List<Competition> participatesToCompetitions(List<Long> ids) {
        return competitionRepository.findByCompetitionIdIn(ids);
    }

    private void updateCompetitionsFundraisingBudget(List<Long> competitions) {
        for (Long competitionId : competitions) {
            Competition competition = competitionRepository.getById(competitionId);

            Double existingFunds = competition.getRaisedMoney();
            competition.setRaisedMoney(existingFunds + competition.getTicketFee());

            competitionRepository.save(competition);
        }
    }

    /*
        the user can update his participation to competitions only by adding more competitions, meaning
        that the already bought ticket is non-refundable and the user can not withdraw from a competition
     */
    @Override
    public void updateUser(UserDto userDto) {
        User user = getByCnp(userDto.getCnp());

        user.setName(userDto.getName());
        user.setAge(userDto.getAge());
        user.setGender(userDto.getGender());
        user.setParticipatesToCompetitions(participatesToCompetitions(userDto.getCompetitionsIds()));

        userRepository.save(user);
    }

    @Override
    public void deleteUser(String cnp) {
        userRepository.delete(getByCnp(cnp));
    }

}
