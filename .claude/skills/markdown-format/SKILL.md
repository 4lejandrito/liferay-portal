---

allowed-tools: [Bash, Edit, Glob, Grep, Read, Write]
argument-hint: "[path to Markdown file]"
description: Format a Markdown file to match Liferay conventions — frontmatter order, Title Case headings, braced shell variables, long-form CLI flags, and professional prose. Use when the user asks to format, clean up, polish, or copy-edit a Markdown file destined for the Liferay repository.
name: markdown-format

---

# Markdown Formatter

Apply Liferay Markdown conventions to the target file so its structure, casing, and prose are consistent and professional.

## Input

### Target File

The path to a Markdown file, read from `${ARGUMENTS}`. When `${ARGUMENTS}` is empty, ask the user which file to format.

## Expected Output

### Formatted File

The target file rewritten in place so every convention defined in `.claude/rules/markdown-style.md` holds.

### Summary

A short report grouping the changes by category (frontmatter, headings, prose, shell, lists). When the file already conformed, say so.