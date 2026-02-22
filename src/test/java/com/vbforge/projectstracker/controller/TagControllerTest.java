package com.vbforge.projectstracker.controller;

import com.vbforge.projectstracker.entity.Role;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.service.TagService;
import com.vbforge.projectstracker.util.SecurityUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@DisplayName("TagController Tests")
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private SecurityUtils securityUtils;

    @Test
    @WithMockUser
    @DisplayName("Should list all tags")
    void shouldListAllTags() throws Exception {
        User user = User.builder().id(1L).username("testuser").role(Role.USER).build();
        Tag tag1 = Tag.builder().id(1L).name("Spring Boot").color("#28a745").owner(user).build();
        
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(tagService.getAllTagsOrderedByPopularity(user)).thenReturn(List.of(tag1));

        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("tags"))
                .andExpect(model().attributeExists("tags", "totalTags", "tagDTO"))
                .andExpect(model().attribute("totalTags", 1));
    }

    @Test
    @WithMockUser
    @DisplayName("Should create tag successfully")
    void shouldCreateTagSuccessfully() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(tagService.existsByName(anyString(), any())).thenReturn(false);

        mockMvc.perform(post("/tags/add")
                        .with(csrf())
                        .param("name", "New Tag")
                        .param("color", "#000000")
                        .param("description", "Test description"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tags"))
                .andExpect(flash().attributeExists("success"));

        verify(tagService).saveTag(any(Tag.class));
    }

    @Test
    @WithMockUser
    @DisplayName("Should not create duplicate tag")
    void shouldNotCreateDuplicateTag() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(tagService.existsByName("Existing", user)).thenReturn(true);

        mockMvc.perform(post("/tags/add")
                        .with(csrf())
                        .param("name", "Existing")
                        .param("color", "#000000"))
                .andExpect(status().isOk())
                .andExpect(view().name("tags"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(tagService, never()).saveTag(any());
    }

    @Test
    @WithMockUser
    @DisplayName("Should update tag successfully")
    void shouldUpdateTagSuccessfully() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);

        mockMvc.perform(post("/tags/1/update")
                        .with(csrf())
                        .param("name", "Updated Tag")
                        .param("color", "#ff0000")
                        .param("description", "Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tags"))
                .andExpect(flash().attributeExists("success"));

        verify(tagService).updateTag(eq(1L), any(Tag.class), eq(user));
    }

    @Test
    @WithMockUser
    @DisplayName("Should delete tag successfully")
    void shouldDeleteTagSuccessfully() throws Exception {
        User user = User.builder().id(1L).username("testuser").build();
        when(securityUtils.getCurrentUser()).thenReturn(user);

        mockMvc.perform(post("/tags/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tags"))
                .andExpect(flash().attributeExists("success"));

        verify(tagService).deleteTag(1L, user);
    }
}
