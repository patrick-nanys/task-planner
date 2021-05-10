package com.kivik.taskplanner.utils.data;

import com.kivik.taskplanner.entities.Privilege;
import com.kivik.taskplanner.entities.Role;
import com.kivik.taskplanner.entities.User;
import com.kivik.taskplanner.services.RoleService;
import com.kivik.taskplanner.services.SafeCreateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Initializer {
    private final static Logger LOGGER = Logger.getLogger(Initializer.class.getName());

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SafeCreateService safeCreateService;

    public void setupRolesAndPrivileges() {
        Privilege user_privilege = safeCreateService.createPrivilegeIfNotFound(Privilege.USER);
        Privilege user_admin_privilege = safeCreateService.createPrivilegeIfNotFound(Privilege.USER_ADMIN);
        Privilege task_admin_privilege = safeCreateService.createPrivilegeIfNotFound(Privilege.TASK_ADMIN);
        Privilege team_admin_privilege = safeCreateService.createPrivilegeIfNotFound(Privilege.TEAM_ADMIN);
        Privilege role_admin_privilege = safeCreateService.createPrivilegeIfNotFound(Privilege.ROLE_ADMIN);
        Privilege privilege_admin_privilege = safeCreateService.createPrivilegeIfNotFound(Privilege.PRIVILEGE_ADMIN);
        Privilege profile_admin_privilege = safeCreateService.createPrivilegeIfNotFound(Privilege.PROFILE_ADMIN);

        ArrayList<Privilege> privileges = new ArrayList<>();

        privileges.add(user_privilege);
        safeCreateService.createRoleIfNotFound(Role.ROLE_USER, privileges);

        privileges.add(user_admin_privilege);
        privileges.add(task_admin_privilege);
        privileges.add(team_admin_privilege);
        safeCreateService.createRoleIfNotFound(Role.ROLE_ADMIN, privileges);

        privileges.add(role_admin_privilege);
        privileges.add(privilege_admin_privilege);
        privileges.add(profile_admin_privilege);
        safeCreateService.createRoleIfNotFound(Role.ROLE_SUPER_ADMIN, privileges);

        LOGGER.log(Level.INFO, "Roles and privileges are successfully set up");
    }

    public void setupUsers() {
        User admin = new User();
        admin.setFirstName("-");
        admin.setLastName("-");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(Collections.singletonList(roleService.findByName(Role.ROLE_ADMIN)));

        User superAdmin = new User();
        superAdmin.setFirstName("-");
        superAdmin.setLastName("-");
        superAdmin.setEmail("super_admin@gmail.com");
        superAdmin.setPassword(passwordEncoder.encode("super_admin"));
        superAdmin.setRoles(Collections.singletonList(roleService.findByName(Role.ROLE_SUPER_ADMIN)));

        safeCreateService.createUserIfNotFound(admin);
        safeCreateService.createUserIfNotFound(superAdmin);
    }
}
