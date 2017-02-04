package pl.put.splitit.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.put.splitit.SplitItApp;
import pl.put.splitit.domain.User;
import pl.put.splitit.domain.UserGroup;
import pl.put.splitit.repository.UserGroupRepository;
import pl.put.splitit.service.UserGroupService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserGroupResource REST controller.
 *
 * @see UserGroupResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SplitItApp.class)
public class UserGroupResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final Boolean DEFAULT_IS_PRIVATE = false;
    private static final Boolean UPDATED_IS_PRIVATE = true;

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    @Inject
    private UserGroupRepository userGroupRepository;

    @Inject
    private UserGroupService userGroupService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restUserGroupMockMvc;

    private UserGroup userGroup;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserGroupResource userGroupResource = new UserGroupResource();
        ReflectionTestUtils.setField(userGroupResource, "userGroupService", userGroupService);
        this.restUserGroupMockMvc = MockMvcBuilders.standaloneSetup(userGroupResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserGroup createEntity(EntityManager em) {
        return createEntity(em, DEFAULT_NAME);
    }

    public static UserGroup createEntity(EntityManager em, String name) {
        UserGroup userGroup = new UserGroup()
            .name(name)
            .isPrivate(DEFAULT_IS_PRIVATE)
            .creationDate(DEFAULT_CREATION_DATE);
        // Add required entity
        User owner = UserResourceIntTest.createEntity(em);
        em.persist(owner);
        em.flush();
        userGroup.setOwner(owner);
        return userGroup;
    }


    @Before
    public void initTest() {
        userGroup = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserGroup() throws Exception {
        int databaseSizeBeforeCreate = userGroupRepository.findAll().size();

        // Create the UserGroup

        restUserGroupMockMvc.perform(post("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userGroup)))
            .andExpect(status().isCreated());

        // Validate the UserGroup in the database
        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeCreate + 1);
        UserGroup testUserGroup = userGroups.get(userGroups.size() - 1);
        assertThat(testUserGroup.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUserGroup.isIsPrivate()).isEqualTo(DEFAULT_IS_PRIVATE);
        assertThat(testUserGroup.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userGroupRepository.findAll().size();
        // set the field null
        userGroup.setName(null);

        // Create the UserGroup, which fails.

        restUserGroupMockMvc.perform(post("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userGroup)))
            .andExpect(status().isBadRequest());

        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsPrivateIsRequired() throws Exception {
        int databaseSizeBeforeTest = userGroupRepository.findAll().size();
        // set the field null
        userGroup.setIsPrivate(null);

        // Create the UserGroup, which fails.

        restUserGroupMockMvc.perform(post("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userGroup)))
            .andExpect(status().isBadRequest());

        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = userGroupRepository.findAll().size();

        // Create the UserGroup, which fails.

        restUserGroupMockMvc.perform(post("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userGroup)))
            .andExpect(status().isBadRequest());

        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUserGroups() throws Exception {
        // Initialize the database
        userGroupRepository.saveAndFlush(userGroup);

        // Get all the userGroups
        restUserGroupMockMvc.perform(get("/api/groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].isPrivate").value(hasItem(DEFAULT_IS_PRIVATE.booleanValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    public void getUserGroup() throws Exception {
        // Initialize the database
        userGroupRepository.saveAndFlush(userGroup);

        // Get the userGroup
        restUserGroupMockMvc.perform(get("/api/groups/{id}", userGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.isPrivate").value(DEFAULT_IS_PRIVATE.booleanValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUserGroup() throws Exception {
        // Get the userGroup
        restUserGroupMockMvc.perform(get("/api/groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserGroup() throws Exception {
        // Initialize the database
        userGroupService.save(userGroup);

        int databaseSizeBeforeUpdate = userGroupRepository.findAll().size();

        // Update the userGroup
        UserGroup updatedUserGroup = userGroupRepository.findOne(userGroup.getId());
        updatedUserGroup
            .name(UPDATED_NAME)
            .isPrivate(UPDATED_IS_PRIVATE)
            .creationDate(UPDATED_CREATION_DATE);

        restUserGroupMockMvc.perform(put("/api/groups")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserGroup)))
            .andExpect(status().isOk());

        // Validate the UserGroup in the database
        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeUpdate);
        UserGroup testUserGroup = userGroups.get(userGroups.size() - 1);
        assertThat(testUserGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUserGroup.isIsPrivate()).isEqualTo(UPDATED_IS_PRIVATE);
        assertThat(testUserGroup.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    public void deleteUserGroup() throws Exception {
        // Initialize the database
        userGroupService.save(userGroup);

        int databaseSizeBeforeDelete = userGroupRepository.findAll().size();

        // Get the userGroup
        restUserGroupMockMvc.perform(delete("/api/groups/{id}", userGroup.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<UserGroup> userGroups = userGroupRepository.findAll();
        assertThat(userGroups).hasSize(databaseSizeBeforeDelete - 1);
    }
}
