package com.fares7elsadek.syncspace.user.seeder;

import com.fares7elsadek.syncspace.user.enums.ServerRoles;
import com.fares7elsadek.syncspace.user.model.Roles;
import com.fares7elsadek.syncspace.user.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RolesSeeder implements CommandLineRunner {

    private final RolesRepository rolesRepository;

    @Override
    public void run(String... args) throws Exception {
        if(rolesRepository.count()==0){
            List<Roles> roles = new ArrayList<>();
            roles.add(Roles.builder().name(ServerRoles.ADMIN.toString()).build());
            roles.add(Roles.builder().name(ServerRoles.OWNER.toString()).build());
            roles.add(Roles.builder().name(ServerRoles.USER.toString()).build());
            rolesRepository.saveAll(roles);
        }
    }
}
